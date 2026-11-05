package dev.chromelab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.junit.jupiter.api.Assertions.*;

/** Checks configuration selection, not database connectivity or migration execution. */
class PostgresConfigurationTest {
    @Test void postgresProfileReenablesDatabaseAutoconfiguration() {
        new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withPropertyValues("spring.profiles.active=postgres", "DB_PASSWORD=local-test")
            .run(context -> {
                var excluded = Binder.get(context.getEnvironment())
                    .bind("spring.autoconfigure.exclude", Bindable.listOf(String.class)).orElse(java.util.List.of());
                assertTrue(excluded.isEmpty());
                assertEquals("local-test", context.getEnvironment().getProperty("spring.datasource.password"));
                assertEquals("true", context.getEnvironment().getProperty("spring.flyway.enabled"));
            });
    }
}
