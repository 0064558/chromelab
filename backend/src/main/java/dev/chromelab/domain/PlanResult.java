package dev.chromelab.domain;

import java.util.List;
import java.util.Map;

public record PlanResult(String rulesetId, String status, boolean usesReset, int unspentNow,
                         int pointsNeeded, int pointsAvailable, int shortfall, Integer earliestLevel,
                         Map<String, Integer> finalAttributes, List<Step> steps, List<String> explanations) {
    public record Step(int level, String attribute, int from, int to) {}
}
