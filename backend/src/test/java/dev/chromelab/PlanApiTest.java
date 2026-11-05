package dev.chromelab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlanApiTest {
    @Autowired MockMvc mvc;
    private static final String REQUEST = """
        {"rulesetId":"attributes-2.0-v1","level":23,"targetLevel":40,"phantomLiberty":false,
         "resetAvailable":false,"allowReset":false,
         "attributes":{"body":10,"reflexes":10,"technicalAbility":9,"intelligence":8,"cool":7},
         "targetAttributes":{"body":15,"reflexes":20,"technicalAbility":9,"intelligence":3,"cool":3}}
        """;
    @Test void servesPlanWithoutDatabase() throws Exception {
        mvc.perform(post("/api/v1/plans").contentType(MediaType.APPLICATION_JSON).content(REQUEST))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("REACHABLE"))
            .andExpect(jsonPath("$.earliestLevel").value(38)).andExpect(jsonPath("$.steps.length()").value(15));
    }
    @Test void livenessIsExplicitlyAvailable() throws Exception {
        mvc.perform(get("/api/v1/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ok"));
    }
    @Test void rejectsMalformedUnknownMissingAndCoercedFields() throws Exception {
        String[] invalid = {"{", "{}", "null", REQUEST + " {}", REQUEST + " trailing", REQUEST.replace("\"level\":23", "\"level\":23.7"),
            REQUEST.replace("\"level\":23", "\"level\":\"23\""),
            REQUEST.replace("\"level\":23", "\"level\":23,\"extra\":1"),
            REQUEST.replace("\"phantomLiberty\":false", "\"phantomLiberty\":\"false\""),
            REQUEST.replace("\"body\":10", "\"body\":10.2"),
            REQUEST.replace("\"resetAvailable\":false,", ""),
            REQUEST.replace("\"body\":10", "\"body\":null")};
        for(String body:invalid) mvc.perform(post("/api/v1/plans").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
