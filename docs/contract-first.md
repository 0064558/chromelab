# Contrato da fatia 01 — atributos (2026-11-05)

Status: contrato inicial implementável. A especificação completa do MVP inclui perks; esta primeira fatia NÃO atesta viabilidade de equipamentos/perks.

POST `/api/v1/plans`, JSON:
```
{"rulesetId":"attributes-2.0-v1","level":23,"targetLevel":40,"phantomLiberty":false,"resetAvailable":false,"allowReset":false,"attributes":{"body":10,"reflexes":10,"technicalAbility":9,"intelligence":8,"cool":7},"targetAttributes":{"body":15,"reflexes":20,"technicalAbility":9,"intelligence":3,"cool":3}}
```
Todos os campos obrigatórios; atributos têm exatamente cinco chaves e inteiros 3..20. Objetivos são mínimos, não distribuição exata. Nível 1..50 (base) ou 1..60 (DLC); alvo >= atual. A soma atual pode ser menor que level+21: a diferença é o saldo não gasto. Soma maior é inválida. Em nível 1 cada atributo atual <=6 (criação); objetivo não tem essa restrição pois pode representar investimento futuro, mas o plano não investe acima de 6 enquanto permanecer no nível 1.

200 JSON:
```
{"rulesetId":"attributes-2.0-v1","status":"REACHABLE","usesReset":false,"unspentNow":0,"pointsNeeded":15,"pointsAvailable":17,"shortfall":0,"earliestLevel":38,"finalAttributes":{"body":15,"reflexes":20,"technicalAbility":9,"intelligence":8,"cool":7},"steps":[{"level":24,"attribute":"body","from":10,"to":11}],"explanations":["..."]}
```
`steps` no exemplo é abreviado. Status: REACHABLE, REQUIRES_RESET, UNREACHABLE. earliestLevel nullable. Ordem fixa body/reflexes/technicalAbility/intelligence/cool; gastar saldo antes de subir de nível; cada step = um ponto. Não gastar excedentes após atingir mínimos. Comparar primeiro rota sem reset; se inviável e resetAvailable AND allowReset, recomputar desde 3 em cada atributo usando orçamento total atual. Só escolher reset se ele viabilizar o alvo; caso contrário retornar UNREACHABLE sem passos. Reset nunca acontece no jogo: usesReset descreve uma simulação consentida. Sem consentimento, informar alternativa no texto se reset viabilizar, mas status permanece UNREACHABLE. `finalAttributes` é a distribuição ao fim dos steps (ou atual quando inviável). pointsNeeded/Available/shortfall referem-se à rota retornada. REQUIRES_RESET significa rota viável que exige reset. Restrição nível 1 é uma hipótese conservadora desta fatia; não oferecer reset no nível 1.

400 `{ "code":"INVALID_REQUEST", "message":"..." }` para JSON/tipos/campos inválidos, orçamento, DLC/nível, regras desconhecidas. Campos desconhecidos rejeitados. Sem salvar automaticamente. GET `/api/v1/health` -> `{"status":"ok"}` (liveness, não readiness de banco).

Fonte de verdade: motor Java puro; UI apenas coleta/exibe. Sem aleatoriedade, relógio, LLM ou rede no algoritmo. Persistência e catálogo de perks são incrementos separados. Perfil padrão deve iniciar sem banco; perfil `postgres` habilita Flyway/JDBC para migrações preparatórias. Testes cobrem conservação de pontos, caps, determinismo, reset, saldo, alvo já atingido, inválidos e nível 1.
