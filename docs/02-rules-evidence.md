# Registro de regras e evidências

Consultado em 2026-11-05. `attributes-2.0-v1` é o identificador de um conjunto experimental baseado no sistema 2.0; não significa suporte certificado ao patch atual. O plano não consulta páginas na execução. Teste unitário prova implementação da regra cadastrada, não a veracidade da regra no jogo.

## Fontes consultadas
- S01: [CDPR — Update 2.0, 21/09/2023](https://www.cyberpunk.net/en/news/49060/update-2-0). Confirma reformulação de perks/skills e possibilidade de reset único.
- S02: [CDPR — Build Planner, 14/09/2023](https://www.cyberpunk.net/en/news/48958/build-planner-craft-your-cyberpunk-2077-and-phantom-liberty-builds). Confirma reembolso de perks fora de combate e finalidade do planner. Não usar sua redação sobre saves existentes para excluir automaticamente novos personagens.
- S03: [CDPR — REDstreams recap, 26/08/2023](https://www.cyberpunk.net/en/news/48700/redstreams-brief-summary-and-recap). Explicita reset único também em novas campanhas; pontos Relic obtidos separadamente.
- S04: [Planner oficial](https://www.cyberpunk.net/en/build-planner). Página localizada, mas a extração de texto não expôs o estado interativo completo; snippet de busca não conta como validação dos limites.
- S05: [Game booklet oficial](https://www.cyberpunk.net/en/news/49789/cyberpunk-2077-ultimate-edition-game-booklet-out-now). Localizado, PDF não inspecionado (limite de tamanho). Não tratado como evidência confirmada.

## Matriz de confiabilidade
| Regra | Afirmação / política | Estado | Evidência / teste de confirmação |
|---|---|---|---|
| G01 | Um reset de atributos por personagem | Confirmada para sistema 2.0 | S01 + S03; usuário informa se já consumiu |
| G02 | Perks podem ser redistribuídos fora de combate | Confirmada para sistema 2.0 | S02; M0 não calcula perks |
| G03 | Relic usa progressão própria | Confirmada para sistema 2.0 | S03; excluída do MVP |
| G04 | Cinco atributos, base 3, teto 20 | Hipótese numérica implementada | Conferir tela do jogo e limites S04; registrar fixture |
| G05 | 7 pontos iniciais, 1 por nível; soma total L+21 | Hipótese numérica implementada | Capturar nível 1 e duas subidas consecutivas; conferir 50/60 no planner |
| G06 | Teto nível 50 base, 60 com DLC | Hipótese numérica implementada | Fixture base sem DLC e com DLC; fonte primária ainda pendente |
| G07 | Máximo 6 por atributo na criação | Hipótese conservadora implementada | Confirmar criação vs investimento após sair da criação, ainda no nível 1 |
| P01 | Alvos são mínimos; preservar excedentes | Política ChromeLab | Testes FR02, não regra do jogo |
| P02 | Ordem de investimento fixa, não ranking de força | Política ChromeLab | Teste de ordem e determinismo |
| P03 | Só simular reset quando expressamente permitido | Política ChromeLab | FR03 e teste de consentimento |

## Gate de publicação de ruleset
Antes de marcar VALIDATED: revisor registra patch/platform/DLC, origem primária ou captura obtida no jogo, data, procedimento, resultado e fixture versionada para cada G04–G07. Confirmar também G01–G03 no patch escolhido. Preservar versão antiga; criar novo ID quando qualquer regra mudar. Não sobrescrever snapshots. Não ativar regras de perks com evidências PENDING. Catálogo não deve misturar patches.

Fixture manual proposta (não executada): criar personagem; capturar cinco valores, saldo, nível e limite de criação; subir ao nível 2; investir e registrar saldo; confirmar teto de nível no planner com/sem DLC; testar reset em save de teste apenas pelo jogador. Não automatizar alterações no save real. Critério de promoção: números reproduzidos em dois cenários e revisão humana.

## Dados não inventados
Não cadastramos nomes/ranks/requisitos de perks, efeitos de Sandevistan, custos de cyberware, DPS ou 'sinergias' sem fontes. Modelos de atributos na interface são sugestões editoriais editáveis. O catálogo de perks é trabalho de M1, com uma ficha de evidência por rank e relacionamento.
