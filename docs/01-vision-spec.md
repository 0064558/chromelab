# ChromeLab — visão e especificação do MVP

Versão 0.1 · 5 de novembro de 2026 · decisões iniciais revisáveis.

## Visão
Responder: **“Com o personagem que já tenho, como chego ao estilo que quero?”** O produto deve explicar se é possível, quais pontos faltam, o que preservar e o que fazer a seguir. Não promete a melhor build universal ou DPS estimado sem evidência.

Público inicial: jogador no meio da campanha, sem mods que alterem progressão, com dificuldade para adaptar um guia à própria distribuição. Idioma inicial pt-BR. Entrada manual; não ler saves. Aplicativo local durante desenvolvimento. Regras fixadas por versão; não aceitar silenciosamente saves de uma versão diferente.

## Escopo e incrementos
**MVP pretendido (M1):** estado atual com atributos e perks de um catálogo pequeno, objetivo, diagnóstico de restrições, rota por nível, alternativas com/sem reset, explicações por regra, salvar e reabrir builds localmente no PostgreSQL. Três modelos editoriais: mobilidade/lâminas, hacking, sobrevivência. Cada modelo só entra quando seus requisitos reais estiverem documentados.

**Fatia implementada agora (M0):** formulário de atributos atuais e mínimos desejados; nível atual/alvo; DLC; disponibilidade e consentimento para simular reset; diagnóstico e sequência de um ponto por ação. Atributos desejados são mínimos: um atributo atual acima do objetivo não precisa diminuir. M0 não conclui que uma build de Sandevistan ou um perk é utilizável. Nenhum catálogo completo ou pontuação subjetiva.

**Fora do MVP:** IA em runtime, chat, mods, importação de saves, contas, comunidade, publicação de builds, inventário completo, dano/combate, compras/localização de equipamentos, Relic, otimização global de todas as combinações. Compartilhamento público requer projeto de acesso separado.

## Fluxo
1. Informar personagem e saldo implícito (orçamento menos atributos gastos). M0 mostra o saldo retornado pelo motor após calcular; uma prévia antes do cálculo fica para M1.
2. Definir nível alvo e mínimos de atributos ou usar modelo editorial editável.
3. Calcular; o backend valida também o estado atual.
4. Mostrar viável, viável com reset autorizado ou inviável; explicar custo, saldo, déficit e nível mais cedo.
5. Percorrer sequência, com atributo anterior e novo valor. Ao editar entrada, remover resultado anterior.
6. M1: salvar explicitamente um snapshot, sem modificar o estado do personagem automaticamente.

## Requisitos rastreáveis
| ID | Requisito | Entrega |
|---|---|---|
| FR01 | Validar campos, limites, orçamento, nível/DLC e versão | M0 |
| FR02 | Preservar atributos atuais sempre que uma rota sem reset existir | M0 |
| FR03 | Simular reset apenas se disponível e autorizado; nunca executar no jogo | M0 |
| FR04 | Produzir passos legais e repetíveis, sem gastar pontos futuros antes do nível | M0 |
| FR05 | Informar déficit quando impossível; não produzir rota parcial como sucesso | M0 |
| FR06 | Mostrar motivo e limite de cobertura em linguagem simples | M0 |
| FR07 | Validar ranks/pré-requisitos de perks com catálogo versionado | M1 |
| FR08 | Registrar pontos de perk externos ao level separadamente | M1 |
| FR09 | Salvar/reabrir snapshot e ruleset exato; recálculo explícito | M1 |
| FR10 | Comparar alternativa preservando atributos e alternativa com reset | M1; M0 informa alternativa no texto |

NFR01: mesmo JSON normalizado + ruleset + engineVersion → mesmo resultado, ordem e explicações. NFR02: motor puro testável sem Spring/DB/rede. NFR03: nenhum cálculo de regra duplicado no React. NFR04: teclado, rótulos, estado de erro/carregamento e layout de 360px. NFR05: meta inicial p95 <200ms para cálculo local, a medir; não é benchmark já obtido. NFR06: solicitações desconhecidas/ilegais falham explicitamente.

## Semântica do motor M0
Orçamento total de atributos `B(L)=15+7+(L-1)=L+21`; saldo `B(L)-sum(atual)`. Necessidade sem reset `D=sum(max(0, alvo[i]-atual[i]))`. Disponível até alvo `saldo+(alvoNivel-nivel)`. Viável se D cabe e a sequência respeita limites. A ordem fixa é body, reflexes, technicalAbility, intelligence, cool: é uma política previsível, NÃO uma alegação de maior desempenho em combate.

Se não for viável, testar reset desde cinco atributos em 3, com orçamento atual devolvido. Preferir rota sem reset; caso contrário só retornar REQUIRES_RESET quando disponível, autorizado e viável. Sem autorização, informar alternativa em texto. Sem rota, retornar passos vazios e distribuição atual. Não gastar excedentes após atingir mínimos.

Nível 1: hipótese conservadora de criação, máximo atual 6 e nenhum passo acima de 6 no nível 1; pontos que não cabem são adiados ao nível 2. Não oferecer reset no nível 1. Hipótese explicitamente pendente de validação no jogo; não publicar M0 como simulação certificada.

## Evolução para perks (M1)
Representar requisito como AND de predicados: atributo mínimo, rank anterior, perks/ranks antecedentes e DLC. Não inferir dependência só pela posição visual. Catálogo valida referências e ciclos antes de ser publicado. Fechar transitivamente os pré-requisitos do objetivo, contabilizar ranks já adquiridos e custos faltantes, ordenar topologicamente com desempate por ID. Antes de cada ação verificar orçamento e todos os predicados no estado simulado.

Separar pontos ganhos por nível dos pontos extras que o jogador declara ter obtido; não prever shards/skill progression como garantidos. Reembolso de perks não autoriza remoção de pré-requisitos ainda usados: remover dependentes primeiro e validar novamente. Catálogo parcial deve rejeitar IDs fora dele, inclusive no personagem atual, em vez de fingir cobertura completa. Restrições não modeladas → resultado 'cobertura insuficiente', não 'inviável no jogo'.

## Validação de valor
Testar com três jogadores: iniciante, mudança de estilo no meio da campanha e adaptação de guia. Tarefa igual no ChromeLab e ferramenta atual. Registrar tempo até primeira ação, compreensão do reset e se o próximo investimento foi claro. Critério exploratório: 3/3 identificam a próxima ação e explicam a restrição; pelo menos 2/3 preferem o fluxo. É uma hipótese de produto, não validação já realizada.
