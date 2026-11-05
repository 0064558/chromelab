package dev.chromelab.domain;

import java.util.*;
import static dev.chromelab.domain.PlanResult.Step;

/** Pure deterministic allocation; targets are minima, never automatic respecs. */
public final class PlanEngine {
    public static final String RULESET = "attributes-2.0-v1";
    public static final List<String> ORDER = List.of("body", "reflexes", "technicalAbility", "intelligence", "cool");

    public PlanResult plan(PlanRequest request) {
        validate(request);
        int unspent = request.level() + 21 - sum(request.attributes());
        Route original = route(request, request.attributes(), unspent);
        if (original.reachable()) return result(original, false, unspent, List.of(
            "Objetivos mínimos alcançáveis preservando os atributos atuais.",
            "Ordem sugerida fixa; não representa otimização de dano, perks ou equipamentos."));

        Route reset = null;
        if (request.level() > 1 && request.resetAvailable()) {
            Map<String, Integer> base = new LinkedHashMap<>();
            ORDER.forEach(key -> base.put(key, 3));
            reset = route(request, base, request.level() + 6);
            if (request.allowReset() && reset.reachable()) return result(reset, true, unspent, List.of(
                "Simulação exige a redefinição única de atributos informada como disponível.",
                "Nenhuma alteração foi realizada no jogo; confirme a disponibilidade no seu personagem."));
        }
        List<String> explanations = new ArrayList<>();
        explanations.add("Os objetivos mínimos não são alcançáveis no nível alvo pela rota sem redefinição.");
        if (request.targetLevel() == 1 && original.needed() <= original.available())
            explanations.add("Há pontos suficientes, mas a restrição de criação limita cada atributo a 6 no nível 1.");
        if (reset != null && reset.reachable()) explanations.add("Há uma alternativa com redefinição. Autorize a simulação para visualizar essa rota.");
        else explanations.add("Reduza os objetivos ou aumente o nível alvo dentro do limite da edição selecionada.");
        return result(original, false, unspent, explanations);
    }

    private PlanResult result(Route route, boolean reset, int unspent, List<String> explanations) {
        return new PlanResult(RULESET, route.reachable() ? (reset ? "REQUIRES_RESET" : "REACHABLE") : "UNREACHABLE",
            reset, unspent, route.needed(), route.available(), Math.max(0, route.needed() - route.available()),
            route.reachable() ? route.lastLevel() : null, route.attributes(), route.steps(), List.copyOf(explanations));
    }

    private Route route(PlanRequest r, Map<String,Integer> start, int initialPoints) {
        Map<String,Integer> current = new LinkedHashMap<>();
        ORDER.forEach(key -> current.put(key, start.get(key)));
        int needed = ORDER.stream().mapToInt(key -> Math.max(0, r.targetAttributes().get(key) - current.get(key))).sum();
        int available = initialPoints + r.targetLevel() - r.level();
        List<Step> steps = new ArrayList<>();
        int points = initialPoints;
        int lastLevel = r.level();
        for (int level = r.level(); level <= r.targetLevel(); level++) {
            if (level > r.level()) points++;
            for (String key : ORDER) {
                int cap = level == 1 ? 6 : 20;
                while (points > 0 && current.get(key) < Math.min(cap, r.targetAttributes().get(key))) {
                    int from = current.get(key);
                    current.put(key, from + 1);
                    steps.add(new Step(level, key, from, from + 1));
                    points--;
                    lastLevel = level;
                }
            }
        }
        boolean reachable = ORDER.stream().allMatch(key -> current.get(key) >= r.targetAttributes().get(key));
        if (!reachable) return new Route(false, needed, available, r.level(), ordered(start), List.of());
        return new Route(true, needed, available, lastLevel, Collections.unmodifiableMap(current), List.copyOf(steps));
    }

    private Map<String,Integer> ordered(Map<String,Integer> source) {
        Map<String,Integer> result = new LinkedHashMap<>();
        ORDER.forEach(key -> result.put(key, source.get(key)));
        return Collections.unmodifiableMap(result);
    }

    private void validate(PlanRequest r) {
        if (r == null || !RULESET.equals(r.rulesetId())) fail("rulesetId ausente ou não suportado.");
        if (r.level() == null || r.targetLevel() == null || r.phantomLiberty() == null || r.resetAvailable() == null || r.allowReset() == null)
            fail("Todos os campos do pedido são obrigatórios.");
        int max = r.phantomLiberty() ? 60 : 50;
        if (r.level() < 1 || r.level() > max || r.targetLevel() < r.level() || r.targetLevel() > max)
            fail("Níveis inválidos para a edição selecionada; alvo deve ser igual ou superior ao atual.");
        validateAttributes(r.attributes());
        validateAttributes(r.targetAttributes());
        if (sum(r.attributes()) > r.level() + 21) fail("Atributos atuais excedem o orçamento do nível informado.");
        if (r.level() == 1 && r.attributes().values().stream().anyMatch(value -> value > 6))
            fail("Nesta versão, atributos atuais no nível 1 não podem exceder 6.");
    }

    private void validateAttributes(Map<String,Integer> attributes) {
        if (attributes == null || !attributes.keySet().equals(new HashSet<>(ORDER))) fail("Informe exatamente os cinco atributos.");
        if (attributes.values().stream().anyMatch(value -> value == null || value < 3 || value > 20))
            fail("Cada atributo deve ser um inteiro entre 3 e 20.");
    }
    private int sum(Map<String,Integer> attributes) { return attributes.values().stream().mapToInt(Integer::intValue).sum(); }
    private static void fail(String message) { throw new IllegalArgumentException(message); }
    private record Route(boolean reachable, int needed, int available, int lastLevel, Map<String,Integer> attributes, List<Step> steps) {}
}
