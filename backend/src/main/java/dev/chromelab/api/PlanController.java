package dev.chromelab.api;

import dev.chromelab.domain.*;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PlanController {
    private final PlanEngine engine = new PlanEngine();
    @PostMapping("/plans") public PlanResult plan(@RequestBody PlanRequest request) { return engine.plan(request); }
    @GetMapping("/health") public Map<String,String> health() { return Map.of("status", "ok"); }
}
