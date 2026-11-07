# Verificação — 2026-11-05

## CL-005 — PostgreSQL real, 2026-11-06

- Aceitação DB01–04 registrada em `03-acceptance.md` antes da implementação. Nenhuma mudança de regra/API ou V1; nenhum endpoint de persistência implementado.
- No diretório backend, com CHROMELAB_POSTGRES_TEST=true e DB_URL/DB_USER/DB_PASSWORD do Compose local: `mvn '-Dmaven.repo.local=../.cache/m2' test`: PASS, 13 testes, zero falhas/erros/ignorados. PostgreSQL real 17.11 em loopback 55432.
- Integração: V1 aplicada uma vez em schema UUID exclusivo; nova instância Flyway não reaplicou e validou checksum; releitura de build/snapshot preservou UUIDs, JSONB real do motor, vínculos, timestamps e ruleset draft. Três FKs inexistentes rejeitadas com SQLSTATE 23503; 12 valores JSONB não objeto rejeitados com 23514.
- Revisão independente por subagente somente leitura: sem achados acionáveis no teste, instruções e aceitação. Timeouts explícitos configurados no JDBC de teste, mas não nas conexões internas do Flyway: limitação operacional registrada.
- Verificação independente: alvo `PostgresIntegrationTest` sem flag → PASS, um teste ignorado; opt-in com URL `jdbc:postgresql://127.0.0.1:1/chromelab` → falha esperada por conexão recusada, um erro, zero ignorados, antes de criar schema. Consulta somente leitura via psql confirmou zero schemas `cl005_test_%` restantes. Classe compilada é posterior à última edição do teste. O último relatório local Surefire da integração corresponde ao negativo esperado, não à suíte positiva anterior.
- Entrega: commit `26a37d541536c8e43511936e7a377e7a26c04404` enviado para `origin/codex/desenvolvimento-continuo`; hash conferido por ls-remote. Fila restante CL-006–011 depende de participação humana na CL-006; nenhuma dependência liberada antecipadamente.
- Ambiente: tentativa inicial de suíte sem acesso autorizado ficou parada após auto-attach Mockito; interrompida, sem resultado conclusivo. Reexecução autorizada passou; nenhuma permissão global alterada. gh ausente; Git Credential Manager já tinha conta 0064558. GitHub issues público retornou HTTP 200 e lista vazia; fila usada é docs/05.
- Não executados nesta CL-005: testes frontend/build, navegador, endpoints de persistência, versionamento/recálculo M1, jogo ou revisão humana CL-006. Não houve mudança em frontend, motor ou API que exigisse repetir sua cobertura além da suíte Java.

## Rechecagem do incremento de UI — 2026-11-06

- Alterações locais preexistentes em `App.tsx`, `App.test.tsx`, `styles.css`, `06-character-ui.md` e capturas preservadas.
- `cd frontend; npm test`: PASS, 1 arquivo, 8 testes, zero falhas (Vitest 3.2.7). Inclui foco/rolagem no sucesso, erro com movimento reduzido e descarte de resposta antiga após edição.
- `cd frontend; npm run build`: PASS, TypeScript e Vite 7.3.7, 30 módulos transformados.
- Subagente revisor em somente leitura por instrução: nenhum achado acionável por inspeção do diff e contexto; nenhuma correção adicional necessária. Sem mudanças de regra/API.
- Não repetidos nesta rodada: navegador real, leitor de tela, zoom e limite desktop de 1200px. Capturas e medições em `06-character-ui.md` são registros da tarefa anterior, não novas observações. A suíte não cobre rejeição antiga ou duas solicitações sobrepostas; a revisão inspecionou as guardas de cancelamento, sem comprovação E2E desses casos.
- Backend e PostgreSQL não reexecutados nesta etapa de UI, pois não foram alterados. Os resultados abaixo continuam sendo históricos de 2026-11-05.

## CL-006 — investigação documental em 2026-11-06

- Somente docs/fixtures alterados nesta etapa; motor, contrato executável, API, migrations e frontend preservados. Não iniciado CL-007, sem publicação/merge ou operação de saves.
- Um subagente pesquisou fontes primárias e outro auditou rastreabilidade em somente leitura por instrução. Revisão final dos documentos/fixture: sem correções obrigatórias; E01/E03/E05 pendentes, E02 parcial, E04 com roteiro e E06 preservado. Isso não substitui revisão humana do gate.
- S01–S03 reabertas por leitura web, escopo histórico 2.0 mantido. S04 não expôs valores interativos. S05 baixado, texto pesquisado e página 9 renderizada/inspecionada; cinco/base 3, sete iniciais e teto 6 na criação têm evidência documental histórica parcial. S06 registra anúncio 2.5, sem confirmar build instalado ou regras numéricas. Apresentação H1 2023 renderizada não confirmou par 50/60.
- Verificação local por Python: PASS, JSON parseável, schemaVersion 1, três templates sem execução/observações, certifiedTarget nulo, status PENDING, revisão humana pendente, hash/tamanho S05 conferidos e referências CL-006 presentes em cinco documentos. `git diff --check`: PASS, apenas avisos de conversão LF/CRLF do Git.
- Falhas de ambiente: Firecrawl sem resolução DNS; fallback web. Leitor web recusou S05 por tamanho; download local com acesso autorizado e bibliotecas PDF já instaladas resolveu a leitura. Caches/PDFs/renders em `.firecrawl/` ignorado; nenhuma credencial/serviço adicionado.
- Não executados: cenários no jogo, limites reais base/DLC, investigação comportamental de shards, reset, revisão humana, novos testes Java/API/PostgreSQL ou E2E. Mudança desta etapa é documental, sem novas regras para testar no motor.
- Alvo desejado informado pelo jogador: última versão/família 2.5 com Phantom Liberty. Plataforma/build exato não observado. CL-006 permanece PENDING e ruleset experimental; roteiro do jogador em `07-cl-006-validation.md` e registros em `fixtures/cl-006/evidence-v1.json`.

Projeto recuperado e copiado para a pasta atual, sem substituir arquivos preexistentes (a pasta estava vazia). Caches locais foram reaproveitados e estão ignorados no Git.

## Resultados observados
- `mvn test`: PASS, 12 testes, zero falhas/erros. Inclui API com MockMvc, coerção estrita, conteúdo após JSON, motor e seleção de perfil PostgreSQL.
- Motor: 500 entradas geradas com semente fixa; reaplica passos conferindo from, +1, conservação de pontos e estado final. Um cenário com reset também é reaplicado desde a base.
- `npm test`: PASS, 5 testes de UI, incluindo chamada contratada, erros e descarte de respostas obsoletas.
- `npm run build`: PASS, TypeScript e Vite. Repetido após configuração de proxy opcional.
- PostgreSQL17 real via Compose: Spring/Flyway iniciou com perfil postgres e aplicou V1 em esquema vazio com sucesso. API iniciou na porta18080. Porta55432 escolhida para PostgreSQL porque5432 estava indisponível.
- Banco: não foram testados endpoints de persistência (ainda não implementados), round-trip de snapshots ou reinício com migration existente.
- Integração HTTP através de Vite: tentativa falhou por timeout de conexão neste ambiente; não alegar integração de navegador concluída.
- Revisão visual: não concluída. Navegador embutido falhou por timeout; ferramenta Windows interrompeu a tentativa por não conseguir confirmar a URL. Nenhuma ação posterior de navegador foi executada.

## Revisão independente
Revisor inspecionou motor e UI. Correções incorporadas: rejeitar tokens após o objeto JSON, ampliar replay de invariantes e ajustar spec para mostrar saldo após calcular em M0. Nenhum erro de orçamento/reset foi encontrado por inspeção; isso não equivale a certificação das regras do jogo.

## Pendências
CL-006: validar G04–G07 no jogo e fixar patch. CL-007–011: catálogo de perks, motor de pré-requisitos, persistência de builds e teste de fluxo completo com jogadores. Antes de anunciar M1 completo, concluir esses critérios.

## Ambiente
Java25.0.2 compilando release21; Maven3.9.16; Node24.20.0; Spring Boot3.5.16; Vite7.3.7. Testes Java precisaram do acesso permitido ao mecanismo de instrumentação Mockito. Nenhuma configuração global de permissões foi alterada. Postman instalado não reconheceu `status`; os checks usaram JUnit/MockMvc/Vitest.
