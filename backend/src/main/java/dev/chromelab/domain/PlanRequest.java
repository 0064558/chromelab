package dev.chromelab.domain;

import java.util.Map;

public record PlanRequest(String rulesetId, Integer level, Integer targetLevel,
                          Boolean phantomLiberty, Boolean resetAvailable, Boolean allowReset,
                          Map<String, Integer> attributes, Map<String, Integer> targetAttributes) {}
