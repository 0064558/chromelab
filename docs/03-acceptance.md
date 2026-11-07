# Critérios de aceitação e cenários

Ordem dos vetores: body, reflexes, technicalAbility, intelligence, cool. Estes cenários verificam o contrato experimental, não substituem validação no jogo.

| ID | Entrada / condição | Resultado esperado |
|---|---|---|
| AC01 / FR01 | L23, atual [10,10,9,8,7], alvo L40 [15,20,9,3,3] | REACHABLE; 15 pontos, saldo0, disponível17; primeiro body10→11 L24; último reflexes19→20 L38; final [15,20,9,8,7] |
| AC02 / FR04 | Mesmo estado, alvo [10,10,9,3,3] | REACHABLE; zero passos; earliestLevel23; manter excedentes |
| AC03 / FR04 | L23, atual [10,10,9,3,3], alvo [15,10,9,3,3], alvo L23 | Saldo9; cinco passos em L23; quatro pontos permanecem |
| AC04 / FR03 | L50, atual [20,20,20,8,3], alvo [3,3,3,20,20], alvo L50 | Sem reset: UNREACHABLE, déficit29, passos vazios |
| AC05 / FR03 | AC04 com resetAvailable=true, allowReset=true | REQUIRES_RESET; final [3,3,3,20,20]; 34 passos em L50; nenhum reset executado no jogo |
| AC06 / FR03 | AC04 disponível, mas allowReset=false | UNREACHABLE; texto pode sugerir consentimento, não retornar rota com reset |
| AC07 / FR05 | L50 alvo cinco atributos20 | UNREACHABLE mesmo com reset; déficit29 |
| AC08 / FR01 | DLC false + alvo51; alvo<atual; soma atual >L+21 | 400 INVALID_REQUEST |
| AC09 / FR01 | Atributo2 ou21, fracionário, null, string numérica, campo extra, ruleset desconhecido | 400, sem stack trace |
| AC10 / NFR01 | Repetir a mesma entrada | Respostas e ordem idênticas |
| AC11 / FR04 | Aplicar cada passo de qualquer resultado viável | From corresponde ao estado; +1; mínimo/máximo válidos; custo acumulado ≤ saldo + níveis ganhos; final satisfaz mínimos |
| AC12 / FR04 | L1 atual[6,6,4,3,3], alvo L2 [7,6,4,3,3] | Único incremento body6→7 em L2 |
| AC13 / FR04 | L1 atual[3,3,3,3,3], alvo L1 [7,3,3,3,3] | Não propor incremento acima de6 no nível1, mesmo havendo saldo; inviável sob G07 |
| AC14 / UI | Mudar entrada após resultado; falha de rede | Resultado obsoleto removido; erro legível; formulário permanece editável |
| AC15 / UI | Teclado, tela estreita, submit pendente | Rótulos associados; sem dupla solicitação; status anunciado |

M1: testes adicionais obrigatórios antes de anunciar MVP completo: rank inválido, predecessor ausente, ciclo/referência inexistente no catálogo, pontos extras de perk, remoção de dependentes, incompatibilidade DLC, catálogo parcial, snapshot round-trip em PostgreSQL real, migração em banco vazio e existente, concorrência/versão otimista ao editar, recálculo preservando versão original.

Definition of Done por tarefa: requisito e contrato atualizados; testes relevantes passam; revisor verifica negativos e fonte de dados; nenhuma promessa maior que cobertura; instruções de execução reproduzíveis; limitações registradas. Status real dos checks em `verification.md`.

## CL-005 — infraestrutura PostgreSQL

- DB01: executar V1 pelo Flyway em schema vazio e exclusivo do teste num PostgreSQL real; repetir migrate sem reaplicar V1 nem perder dados.
- DB02: inserir e reler ruleset, build e snapshot com UUID, JSONB e vínculo corretos; usar payload real do motor experimental, sem anunciar persistência HTTP ou certificação do jogo.
- DB03: provar rejeição de FKs inexistentes em build/snapshot e de JSONB não objeto; manter V1 intacta.
- DB04: execução opt-in explícita, falhar se banco solicitado estiver indisponível, isolar e remover somente schema criado pelo teste; execução padrão continua sem banco.

Escopo: teste da estrutura preparatória. CRUD, engineVersion/schemaVersion persistidos e recálculo continuam CL-010; não mudar regra nem API na CL-005.

## CL-006 — aceitação da evidência (2026-11-06)

- E01: registrar patch/build exato, plataforma, DLC e ausência de mods que alterem progressão. Família 2.5 + DLC é intenção do jogador, não ambiente observado.
- E02: G04–G07 têm fonte primária localizada por seção/página, procedimento, resultado observado e fixture versionada. Separar documento histórico de captura no patch alvo. Template PENDING não conta como cenário executado.
- E03: reproduzir números em dois cenários identificados; mapear cobertura de cada subafirmação (base/teto, criação/progressão, base/DLC), sem usar repetição parcial para certificar o conjunto.
- E04: G07 distingue criação de investimento pós-criação no nível 1. Sem saldo, tentativa bloqueada não prova teto 6. G05 investiga a menção a Attribute Shards em S01 sem presumir efeito.
- E05: reconfirmar G01–G03 no patch escolhido e registrar revisão humana com nome, data, decisão e fixtures examinadas. Revisão de subagente não substitui esse gate.
- E06: divergência exige registrar regressões propostas e manter motor/contrato atual; mudança de regra requer incremento explícito, spec/aceite antes de código e novo ruleset ID. Sem E01–E05 completos, manter EXPERIMENTAL e CL-006 pendente; não avançar CL-007.

Procedimento e estado real: `07-cl-006-validation.md` e `fixtures/cl-006/evidence-v1.json`.
