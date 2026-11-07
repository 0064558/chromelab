# CL-006 — evidências e roteiro do jogador

Coleta documental: 2026-11-06. Estado: **PENDING**, sem promoção de ruleset. Escopo: evidências e fixtures; motor, API, banco e catálogo não alterados.

## Alvo e resultado da investigação

O jogador deseja a versão mais recente (indicou 2.5) e possui Phantom Liberty. S06 documenta o anúncio da família 2.5 e distribuição a partir de 22/10/2026; isso não determina plataforma, hotfix ou build instalado. Registrar a versão completa exibida no jogo antes de fixar suporte. Não há patch certificado nesta entrega.

Fontes S01–S06 e matriz em `02-rules-evidence.md`. O PDF S05 foi efetivamente baixado: 21.864.547 bytes, 83 páginas, SHA-256 `67796864506c16d115442784120fb312ab25b43f182122d8c8f990a16f045b4d`. Texto pesquisado; página PDF/impressa 9 renderizada e inspecionada, seção WHEN STARTING WITH THE BASE GAME. Esse trecho documenta cinco atributos/base 3, sete pontos iniciais e teto 6 durante criação. A seção WHEN STARTING WITH PHANTOM LIBERTY descreve início direto em nível 15 com atributos predefinidos; por isso essa opção não serve para observar criação/progressão desde nível 1. Não foi encontrada confirmação completa de G04–G07 no texto extraído; não tratar isso como prova de ausência de informação em figuras ou fontes não examinadas.

G07 tem uma lacuna precisa: a fonte limita a criação, enquanto `PlanEngine` restringe todo o nível 1. Não há divergência comprovada no jogo; a extrapolação permanece hipótese. Para G05, S01 menciona Attribute Shards sem efeito detalhado. Não concluir que concedem pontos de atributo nem que não existem fontes extras; esclarecer antes de certificar `L+21` como orçamento completo.

## Roteiro curto — somente o jogador opera o jogo

1. Capture a versão/build no menu, plataforma/loja e estado da expansão. Informe mods que alteram progressão; se houver, esse cenário fica fora do escopo. Registre data e identificador do personagem de teste. Não é necessário enviar save.
2. Em uma campanha de teste, escolha início pela história base, mesmo tendo DLC. Antes de gastar, capture os cinco atributos e saldo; depois distribua os pontos tentando ultrapassar 6 em um atributo e capture o limite. Anote todos os valores antes/depois, inclusive eventual exigência de gastar todos os pontos para sair da criação.
3. Ao entrar no jogo ainda no nível 1, capture atributos/saldo. Se existir ponto disponível, tente passar de 6 e registre o resultado. **Sem ponto disponível, o teste do teto pós-criação é inconclusivo**; não use falta de saldo como confirmação de G07. Não use console, mod, reset ou edição de save para fabricar saldo.
4. Jogue normalmente até níveis 2 e 3 no personagem de teste. Capture atributos e saldo antes/depois de cada subida e de um investimento. Registre qualquer outra recompensa ocorrida no intervalo. Compare `soma(atributos)+saldo` entre capturas; a hipótese é +1 por nível sem outras fontes. Diferença é achado a investigar, não erro a ocultar.
5. Para teto 20 e limite com DLC, registre controles/valores no planner oficial e, quando houver personagem de teste adequado, no jogo. Capture edição/DLC, atributo 20 e tentativa de exceder, nível e limite. Planner sozinho não prova comportamento do patch instalado. Com sua DLC só se observa o cenário com DLC: o teto base 50 continua pendente até fonte primária específica ou cenário sem DLC; não desinstale nada para esta coleta.
6. Repita números em um segundo cenário identificado no mesmo patch/plataforma, anotando quais subafirmações foram de fato repetidas. Não conte duas capturas da mesma ação como dois cenários. Para G01–G03, registre textos/controles de reset, reembolso de perks fora de combate e origem separada de Relic no patch alvo. Inspeção de interface não prova todo comportamento. Nenhum reset precisa ser consumido: se só for possível confirmar por execução, registre pendência para procedimento separado com consentimento explícito do jogador.

Entrega mínima: plataforma/loja, versão/build, DLC/mods, valores e capturas dos passos 1–4. Passos 5–6 podem permanecer pendentes se o cenário não estiver disponível. Não alterar saves reais. O agente não abre o jogo nem opera saves.

## Registro e revisão

`fixtures/cl-006/evidence-v1.json` contém registros documentais já observados e **templates de jogo não executados**. Preencher `observed` apenas com medidas reais, `attachments` com caminhos relativos de capturas, data/contexto e cobertura por regra/subafirmação. Copiar o template para um novo arquivo/ID por execução, preservando este registro inicial. O campo `hypothesis` é expectativa do contrato experimental, nunca resultado.

Um revisor humano deve registrar nome, data, decisão, fontes e IDs de cenários examinados, incluindo G01–G03, antes de qualquer VALIDATED. Patch/platform/DLC e dois cenários precisam estar rastreáveis por afirmação, não só no cabeçalho. A revisão de subagente nesta tarefa avalia documentação, sem validar números no jogo. Critérios E01–E06 em `03-acceptance.md`. Nenhuma execução de jogo nem revisão humana foi registrada nesta coleta.

## Regressões propostas se houver diferença (não executadas)

| Regra | Rastreio atual | Cenário a adicionar/rever após evidência |
|---|---|---|
| G04 | `PlanEngine.validateAttributes`, AC09 | Base/teto observados, rejeitar fora do intervalo e preservar cinco chaves |
| G05 | `plan`/`validate`, AC03/AC11 | Replay níveis 1→2→3 com saldo real; fonte extra confirmada exige revisão de contrato antes de implementação |
| G06 | `validate`, AC08 | Fronteiras 50/51 sem DLC e 60/61 com DLC, somente se confirmadas |
| G07 | `route`/`validate`, AC12/AC13, teste `levelOneRestrictionsAndBankedPoints` | Separar criação e pós-criação no nível 1; orçamento suficiente e bloqueio por cap são condições distintas |

Não alimentar testes do motor com templates como se fossem fixtures observadas. Se uma regra mudar, atualizar spec/contrato/aceitação e criar novo ID; preservar versão antiga. CL-007 não iniciado.

## Limitações de coleta

- Firecrawl instalado com credenciais existentes: coleta falhou por DNS `ENOTFOUND api.firecrawl.dev`; fallback para leitura web. Nenhuma instalação ou nova credencial.
- Planner S04 retornou apenas navegação sem valores interativos; snippets descartados como prova.
- PDF S05 excedeu limite da ferramenta web; download local autorizado e extração/renderização com bibliotecas existentes resolveram a leitura da página 9. PDF e render ficam em `.firecrawl/`, ignorado pelo Git; URL/página/hash ficam versionados.
- [Apresentação H1 2023 da CD PROJEKT](https://www.cdprojekt.com/en/wp-content/uploads-en/2023/08/cd-projekt-group-presentation-h1-2023-earnings.pdf): leitura web sem texto; coordenador baixou e renderizou as 18 páginas para inspeção geral. Slide 6 diz apenas que o limite de nível foi aumentado, sem estabelecer o par 50/60. Não usado para confirmar G06. Post social indicado pelo pesquisador falhou 403; não usado como evidência.
- Não foram executados jogo, reset, teste de shards, cenários base/DLC ou revisão humana. Backend/PostgreSQL não reexecutados por esta mudança documental.
