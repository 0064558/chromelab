package dev.chromelab;

import dev.chromelab.domain.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PlanEngineTest {
    private final PlanEngine engine = new PlanEngine();
    private Map<String,Integer> attrs(int b, int r, int t, int i, int c) {
        return Map.of("body",b,"reflexes",r,"technicalAbility",t,"intelligence",i,"cool",c);
    }
    private PlanRequest request(int level, int target, boolean dlc, boolean reset, boolean allow,
                                Map<String,Integer> current, Map<String,Integer> goal) {
        return new PlanRequest(PlanEngine.RULESET, level, target, dlc, reset, allow, current, goal);
    }
    @Test void exampleConservesPointsAndIsDeterministic() {
        var input = request(23,40,false,false,false,attrs(10,10,9,8,7),attrs(15,20,9,3,3));
        var result = engine.plan(input);
        assertEquals("REACHABLE", result.status());
        assertEquals(38, result.earliestLevel());
        assertEquals(15, result.steps().size());
        assertEquals(attrs(15,20,9,8,7), result.finalAttributes());
        assertEquals(result, engine.plan(input));
        assertEquals(24, result.steps().getFirst().level());
        assertEquals(1, result.steps().getFirst().to() - result.steps().getFirst().from());
    }
    @Test void unspentPointsAreUsedAtCurrentLevel() {
        var result = engine.plan(request(10,10,false,false,false,attrs(3,3,3,3,3),attrs(10,3,3,3,3)));
        assertEquals(16, result.unspentNow());
        assertEquals(10, result.earliestLevel());
        assertTrue(result.steps().stream().allMatch(step -> step.level() == 10));
    }
    @Test void alreadyReachedGoalPreservesInvestments() {
        var result = engine.plan(request(23,40,false,true,true,attrs(10,10,9,8,7),attrs(3,3,3,3,3)));
        assertFalse(result.usesReset());
        assertEquals(23, result.earliestLevel());
        assertEquals(List.of(), result.steps());
    }
    @Test void resetNeedsAvailabilityAndConsentAndOnlyWhenNecessary() {
        var current = attrs(3,3,20,3,3);
        var goal = attrs(20,3,3,3,3);
        var denied = engine.plan(request(11,11,false,true,false,current,goal));
        assertEquals("UNREACHABLE", denied.status());
        assertTrue(denied.explanations().stream().anyMatch(message -> message.contains("Autorize")));
        var unavailable = engine.plan(request(11,11,false,false,true,current,goal));
        assertEquals("UNREACHABLE", unavailable.status());
        var accepted = engine.plan(request(11,11,false,true,true,current,goal));
        assertEquals("REQUIRES_RESET", accepted.status());
        assertEquals(goal, accepted.finalAttributes());
        assertEquals(17, accepted.steps().size());
        assertEquals(17, accepted.pointsAvailable());
        assertValidReplay(accepted, attrs(3,3,3,3,3), 11, 11);
    }
    @Test void impossibleDoesNotReturnPartialPlanOrSpendReset() {
        var result = engine.plan(request(11,11,false,true,true,attrs(3,3,20,3,3),attrs(20,20,20,20,20)));
        assertEquals("UNREACHABLE", result.status());
        assertFalse(result.usesReset());
        assertTrue(result.steps().isEmpty());
        assertNull(result.earliestLevel());
        assertEquals(attrs(3,3,20,3,3), result.finalAttributes());
    }
    @Test void levelOneRestrictionsAndBankedPoints() {
        var current = attrs(3,3,3,3,3);
        var goal = attrs(8,3,3,3,3);
        assertEquals("UNREACHABLE", engine.plan(request(1,1,false,true,true,current,goal)).status());
        var result = engine.plan(request(1,2,false,false,false,current,goal));
        assertEquals("REACHABLE", result.status());
        assertEquals(2, result.earliestLevel());
        assertTrue(result.steps().stream().filter(step -> step.level() == 1).allMatch(step -> step.to() <= 6));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(1,2,false,false,false,attrs(7,3,3,3,3),goal)));
    }
    @Test void rejectsBudgetBoundsMissingUnknownRulesAndReversedLevels() {
        var a = attrs(3,3,3,3,3);
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(1,2,false,false,false,attrs(6,6,6,6,6),a)));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(50,51,false,false,false,a,a)));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(60,61,true,false,false,a,a)));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(20,19,false,false,false,a,a)));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(2,3,false,false,false,attrs(2,3,3,3,3),a)));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(2,3,false,false,false,a,attrs(21,3,3,3,3))));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(request(2,3,false,false,false,Map.of("body",3),a)));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(new PlanRequest("unknown",2,3,false,false,false,a,a)));
        assertThrows(IllegalArgumentException.class, () -> engine.plan(null));
        assertEquals("REACHABLE", engine.plan(request(60,60,true,false,false,a,a)).status());
    }
    @Test void manyValidInputsNeverOverspendOrDecreaseAttributes() {
        Random random = new Random(41);
        for (int sample=0; sample<500; sample++) {
            int level=2+random.nextInt(49), target=level+random.nextInt(51-level);
            Map<String,Integer> current = new LinkedHashMap<>(attrs(3,3,3,3,3));
            int spent=random.nextInt(level+7);
            for(int point=0;point<spent;point++) {
                String key=PlanEngine.ORDER.get(random.nextInt(5));
                if(current.get(key)<20) current.put(key,current.get(key)+1);
            }
            Map<String,Integer> goal = new LinkedHashMap<>();
            for(String key:PlanEngine.ORDER) goal.put(key,3+random.nextInt(18));
            var result=engine.plan(request(level,target,false,false,false,current,goal));
            if(result.status().equals("REACHABLE")) {
                assertValidReplay(result, current, level, target);
                for(String key:PlanEngine.ORDER) {
                    assertTrue(result.finalAttributes().get(key)>=current.get(key));
                    assertTrue(result.finalAttributes().get(key)>=goal.get(key));
                }
            }
        }
    }

    private void assertValidReplay(PlanResult result, Map<String,Integer> start, int level, int targetLevel) {
        Map<String,Integer> replay = new LinkedHashMap<>(start);
        int previousLevel = level;
        int remaining = level + 21 - start.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(remaining + targetLevel - level, result.pointsAvailable());
        for (var step : result.steps()) {
            assertTrue(step.level() >= previousLevel);
            assertTrue(step.level() <= targetLevel);
            remaining += step.level() - previousLevel;
            assertTrue(remaining > 0);
            assertEquals(replay.get(step.attribute()), step.from());
            assertEquals(step.from() + 1, step.to());
            assertTrue(step.to() <= (step.level() == 1 ? 6 : 20));
            replay.put(step.attribute(), step.to());
            remaining--;
            previousLevel = step.level();
            assertEquals(step.level() + 21,
                replay.values().stream().mapToInt(Integer::intValue).sum() + remaining);
        }
        assertEquals(replay, result.finalAttributes());
        assertEquals(previousLevel, result.earliestLevel());
        assertEquals(result.pointsNeeded(), result.steps().size());
    }
}
