package dev.chromelab;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.chromelab.domain.PlanEngine;
import dev.chromelab.domain.PlanRequest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Opt-in contract test of V1 on real PostgreSQL, independent of the future persistence API. */
@EnabledIfEnvironmentVariable(named = "CHROMELAB_POSTGRES_TEST", matches = "true")
class PostgresIntegrationTest {
    private final ObjectMapper json = new ObjectMapper();

    @Test void migratesEmptySchemaThenPreservesRoundTripAndRejectsInvalidReferences() throws Exception {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://127.0.0.1:55432/chromelab");
        String user = System.getenv().getOrDefault("DB_USER", "chromelab");
        String password = System.getenv("DB_PASSWORD");
        assertNotNull(password, "Set DB_PASSWORD explicitly for the opt-in PostgreSQL test");
        String schema = "cl005_test_" + UUID.randomUUID().toString().replace("-", "");
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("connectTimeout", "5");
        properties.setProperty("socketTimeout", "15");
        // CREATE must succeed before entering cleanup; never remove a pre-existing schema.
        try (Connection admin = DriverManager.getConnection(url, properties)) {
            try (var statement = admin.createStatement()) {
                statement.execute("CREATE SCHEMA " + schema);
            }
            try {
                Flyway flyway = Flyway.configure().dataSource(url, user, password)
                    .schemas(schema).defaultSchema(schema).createSchemas(false)
                    .locations("classpath:db/migration").load();
                assertEquals(1, flyway.migrate().migrationsExecuted);

                properties.setProperty("currentSchema", schema);
                UUID buildId = UUID.randomUUID();
                UUID snapshotId = UUID.randomUUID();
                var request = new PlanRequest(PlanEngine.RULESET, 23, 40, false, false, false,
                    attributes(10, 10, 9, 8, 7), attributes(15, 20, 9, 3, 3));
                var result = new PlanEngine().plan(request);
                assertEquals("REACHABLE", result.status());
                String stateJson = json.writeValueAsString(Map.of("level", request.level(),
                    "phantomLiberty", request.phantomLiberty(), "resetAvailable", request.resetAvailable(),
                    "attributes", request.attributes()));
                String requestJson = json.writeValueAsString(request);
                String responseJson = json.writeValueAsString(result);
                try (Connection connection = DriverManager.getConnection(url, properties)) {
                    connection.setSchema(schema);
                    assertEquals(schema, connection.getSchema());
                    execute(connection, "INSERT INTO saved_build(id,name,ruleset_id,character_state) VALUES (?,?,?,?::jsonb)",
                        buildId, "CL-005 round-trip", PlanEngine.RULESET, stateJson);
                    execute(connection, "INSERT INTO plan_snapshot(id,build_id,ruleset_id,request,response) VALUES (?,?,?,?::jsonb,?::jsonb)",
                        snapshotId, buildId, PlanEngine.RULESET, requestJson, responseJson);
                    rejectsInvalidRows(connection, buildId, stateJson, requestJson, responseJson);
                }

                // New Flyway/connection: same schema with existing data must not reapply V1.
                Flyway restarted = Flyway.configure().dataSource(url, user, password)
                    .schemas(schema).defaultSchema(schema).createSchemas(false)
                    .locations("classpath:db/migration").load();
                assertEquals(0, restarted.migrate().migrationsExecuted);
                restarted.validate();
                try (Connection connection = DriverManager.getConnection(url, properties)) {
                    connection.setSchema(schema);
                    assertEquals(schema, connection.getSchema());
                    try (var statement = connection.prepareStatement("""
                        SELECT b.id AS build_id, b.name, b.ruleset_id, b.character_state, b.created_at AS build_created,
                               s.id AS snapshot_id, s.request, s.response, s.created_at AS snapshot_created,
                               r.version, r.status, r.source_url
                        FROM saved_build b JOIN plan_snapshot s ON s.build_id = b.id
                        JOIN rule_set r ON r.id = b.ruleset_id AND r.id = s.ruleset_id
                        WHERE b.id = ? AND s.id = ?
                        """)) {
                        statement.setObject(1, buildId);
                        statement.setObject(2, snapshotId);
                        try (var rows = statement.executeQuery()) {
                            assertTrue(rows.next());
                            assertEquals(buildId, rows.getObject("build_id", UUID.class));
                            assertEquals(snapshotId, rows.getObject("snapshot_id", UUID.class));
                            assertEquals("CL-005 round-trip", rows.getString("name"));
                            assertEquals(PlanEngine.RULESET, rows.getString("ruleset_id"));
                            assertEquals("1", rows.getString("version"));
                            assertEquals("draft", rows.getString("status"));
                            assertEquals("https://www.cyberpunk.net/en/build-planner", rows.getString("source_url"));
                            assertEquals(json.readTree(stateJson), json.readTree(rows.getString("character_state")));
                            assertEquals(json.readTree(requestJson), json.readTree(rows.getString("request")));
                            assertEquals(json.readTree(responseJson), json.readTree(rows.getString("response")));
                            assertNotNull(rows.getTimestamp("build_created"));
                            assertNotNull(rows.getTimestamp("snapshot_created"));
                            assertFalse(rows.next());
                        }
                    }
                    try (var statement = connection.createStatement();
                         var rows = statement.executeQuery("SELECT count(*) FROM flyway_schema_history WHERE success")) {
                        assertTrue(rows.next());
                        assertEquals(1, rows.getInt(1));
                    }
                }
            } finally {
                // Identifier comes only from this test's UUID, never from environment/input.
                try (var statement = admin.createStatement()) {
                    statement.execute("DROP SCHEMA " + schema + " CASCADE");
                }
            }
        }
    }

    private void rejectsInvalidRows(Connection connection, UUID buildId, String state, String request, String response) {
        assertSqlState("23503", () -> execute(connection,
            "INSERT INTO saved_build(id,name,ruleset_id,character_state) VALUES (?,?,?,?::jsonb)",
            UUID.randomUUID(), "invalid FK", "missing-ruleset", state));
        assertSqlState("23503", () -> execute(connection,
            "INSERT INTO plan_snapshot(id,build_id,ruleset_id,request,response) VALUES (?,?,?,?::jsonb,?::jsonb)",
            UUID.randomUUID(), UUID.randomUUID(), PlanEngine.RULESET, request, response));
        assertSqlState("23503", () -> execute(connection,
            "INSERT INTO plan_snapshot(id,build_id,ruleset_id,request,response) VALUES (?,?,?,?::jsonb,?::jsonb)",
            UUID.randomUUID(), buildId, "missing-ruleset", request, response));
        for (String nonObject : new String[]{"[]", "null", "42", "\"text\""}) {
            assertSqlState("23514", () -> execute(connection,
                "INSERT INTO saved_build(id,name,ruleset_id,character_state) VALUES (?,?,?,?::jsonb)",
                UUID.randomUUID(), "invalid JSON type", PlanEngine.RULESET, nonObject));
            assertSqlState("23514", () -> execute(connection,
                "INSERT INTO plan_snapshot(id,build_id,ruleset_id,request,response) VALUES (?,?,?,?::jsonb,?::jsonb)",
                UUID.randomUUID(), buildId, PlanEngine.RULESET, nonObject, response));
            assertSqlState("23514", () -> execute(connection,
                "INSERT INTO plan_snapshot(id,build_id,ruleset_id,request,response) VALUES (?,?,?,?::jsonb,?::jsonb)",
                UUID.randomUUID(), buildId, PlanEngine.RULESET, request, nonObject));
        }
    }

    private static Map<String, Integer> attributes(int body, int reflexes, int technical, int intelligence, int cool) {
        return Map.of("body", body, "reflexes", reflexes, "technicalAbility", technical,
            "intelligence", intelligence, "cool", cool);
    }

    private static void execute(Connection connection, String sql, Object... parameters) throws SQLException {
        try (var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
            assertEquals(1, statement.executeUpdate());
        }
    }

    private static void assertSqlState(String expected, org.junit.jupiter.api.function.Executable action) {
        assertEquals(expected, assertThrows(SQLException.class, action).getSQLState());
    }
}
