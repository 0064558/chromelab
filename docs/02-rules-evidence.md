# Registro de regras e evidências

Consultado inicialmente em 2026-11-05; rechecagem documental CL-006 em 2026-11-06. `attributes-2.0-v1` é o identificador de um conjunto experimental baseado no sistema 2.0; não significa suporte certificado ao patch atual. O plano não consulta páginas na execução. Teste unitário prova implementação da regra cadastrada, não a veracidade da regra no jogo.

## Fontes consultadas
- S01: [CDPR — Update 2.0, 21/09/2023](https://www.cyberpunk.net/en/news/49060/update-2-0). Confirma reformulação de perks/skills e possibilidade de reset único.
- S02: [CDPR — Build Planner, 14/09/2023](https://www.cyberpunk.net/en/news/48958/build-planner-craft-your-cyberpunk-2077-and-phantom-liberty-builds). Confirma reembolso de perks fora de combate e finalidade do planner. Não usar sua redação sobre saves existentes para excluir automaticamente novos personagens.
- S03: [CDPR — REDstreams recap, 26/08/2023](https://www.cyberpunk.net/en/news/48700/redstreams-brief-summary-and-recap). Explicita reset único também em novas campanhas; pontos Relic obtidos separadamente.
- S04: [Planner oficial](https://www.cyberpunk.net/en/build-planner). Página localizada, mas a extração de texto não expôs o estado interativo completo; snippet de busca não conta como validação dos limites.
- S05: [Game booklet oficial, anúncio de 20/12/2023](https://www.cyberpunk.net/en/news/49789/cyberpunk-2077-ultimate-edition-game-booklet-out-now) e [PDF inglês](https://cdn-s-cyberpunk.cdprojektred.com/CP2077-UE-Booklet-EN-1.pdf). Em 2026-11-06 o PDF foi baixado, texto pesquisado e página 9 renderizada/inspecionada: descreve cinco atributos com base 3, sete pontos iniciais e teto 6 durante criação. Não comprova +1 por nível, teto 20, limites 50/60 nem teto pós-criação no nível 1. Documento histórico sem patch/build de teste; detalhes e hash na fixture.
- S06: [CDPR — anúncio Update 2.5, 21/10/2026](https://www.cyberpunk.net/en/news/52120/check-out-whats-coming-to-night-city-in-the-edgerunners-2-update-for-cyberpunk-2077). Informa distribuição a partir de 22/10 em plataformas listadas; não estabelece G04–G07 nem prova qual build está instalada no jogador. Não usado para certificar as regras ou afirmar ausência de hotfix posterior.

## Matriz de confiabilidade
| Regra | Afirmação / política | Estado | Evidência / teste de confirmação |
|---|---|---|---|
| G01 | Um reset de atributos por personagem | Confirmada para sistema 2.0 | S01 + S03; usuário informa se já consumiu |
| G02 | Perks podem ser redistribuídos fora de combate | Confirmada para sistema 2.0 | S02; M0 não calcula perks |
| G03 | Relic usa progressão própria | Confirmada para sistema 2.0 | S03; excluída do MVP |
| G04 | Cinco atributos, base 3, teto 20 | Evidência documental parcial; experimental | S05 p.9 sustenta cinco/base 3 historicamente; teto 20 e confirmação no patch alvo pendentes |
| G05 | 7 pontos iniciais, 1 por nível; soma total L+21 | Evidência documental parcial; experimental | S05 p.9 sustenta sete iniciais; progressão/soma e efeitos de shards pendentes; capturar nível 1 e duas subidas |
| G06 | Teto nível 50 base, 60 com DLC | Hipótese numérica implementada | Nenhuma confirmação primária numérica completa nesta coleta; fixtures base/DLC pendentes |
| G07 | Máximo 6 na criação; motor estende a todo nível 1 | Evidência documental parcial; extensão conservadora | S05 p.9 sustenta criação historicamente; pós-criação no nível 1 não confirmado |
| P01 | Alvos são mínimos; preservar excedentes | Política ChromeLab | Testes FR02, não regra do jogo |
| P02 | Ordem de investimento fixa, não ranking de força | Política ChromeLab | Teste de ordem e determinismo |
| P03 | Só simular reset quando expressamente permitido | Política ChromeLab | FR03 e teste de consentimento |

## Gate de publicação de ruleset
Antes de marcar VALIDATED: revisor registra patch/platform/DLC, origem primária ou captura obtida no jogo, data, procedimento, resultado e fixture versionada para cada G04–G07. Confirmar também G01–G03 no patch escolhido. Preservar versão antiga; criar novo ID quando qualquer regra mudar. Não sobrescrever snapshots. Não ativar regras de perks com evidências PENDING. Catálogo não deve misturar patches.

Fixture manual proposta (não executada): criar personagem; capturar cinco valores, saldo, nível e limite de criação; subir ao nível 2; investir e registrar saldo; confirmar teto de nível no planner com/sem DLC; testar reset em save de teste apenas pelo jogador. Não automatizar alterações no save real. Critério de promoção: números reproduzidos em dois cenários e revisão humana.

## Dados não inventados

CL-006 segue pendente: intenção do jogador = versão mais recente/família 2.5 com Phantom Liberty; plataforma/build exatos ainda desconhecidos. S01–S03 foram reabertas e mantêm apenas a evidência histórica de G01–G03 para 2.0. S01, seção PERKS & SKILLS, menciona Attribute Shards sem detalhar efeito: isto abre uma questão sobre completude de G05, não comprova pontos extras nem autoriza alterar orçamento. Ausência de menção a mudança nas notas 2.5 não comprova invariância. Roteiro, regressões propostas e lacunas em `07-cl-006-validation.md`; registros em `fixtures/cl-006/evidence-v1.json`.
Não cadastramos nomes/ranks/requisitos de perks, efeitos de Sandevistan, custos de cyberware, DPS ou 'sinergias' sem fontes. Modelos de atributos na interface são sugestões editoriais editáveis. O catálogo de perks é trabalho de M1, com uma ficha de evidência por rank e relacionamento.
