# Execução SDD com agentes e PRs pequenos

## Ciclo de uma tarefa
1. Selecionar uma issue abaixo, ler spec/contrato/evidências e listar critérios aplicáveis.
2. Escrever ou atualizar exemplos de aceitação antes de código de regra.
3. Implementar apenas o incremento. Agente sinaliza dúvida de domínio, não inventa dado para passar teste.
4. Rodar testes relevantes e registrar comandos/resultados reais.
5. Outro agente revisa contrato, invariantes e negativos; autor corrige.
6. Abrir PR pequeno quando existir remoto autorizado. Revisão humana decide merge e publicação. Não há PR remoto aberto nesta entrega.

## Backlog importável manualmente como issues
| ID / PR | Resultado e tarefas | Dependências | Aceite / tamanho |
|---|---|---|---|
| CL-001 | Visão, contrato M0, evidências, critérios, arquitetura | — | Docs coerentes; nenhum dado pendente marcado validado; S |
| CL-002 | Motor puro: orçamento, preservação, reset, steps | 001 | AC01–13 e testes de invariantes; M |
| CL-003 | Controller e desserialização estrita; errors; health | 002 | AC08–10 por HTTP; S |
| CL-004 | React formulário e resultado; proxy; erro/loading | 001,003 | AC14–15; M |
| CL-005 | PostgreSQL Compose + migration + round-trip real | 001 | Banco vazio/migração repetida e FK testados; S |
| CL-006 | Revisar G04–G07 no jogo e fixar patch suportado | 001 | Fixtures e reviewer; liberar ruleset validado; S |
| CL-007 | Catálogo mínimo de 10–15 perks, três objetivos | 006 | Cada rank/custo/aresta com fonte; sem ciclos/IDs órfãos; M |
| CL-008 | Motor perks: fechamento, reembolso, ordem, extras | 002,007 | Testes M1 de `03-acceptance.md`; M; dividir em validação e planejamento se crescer |
| CL-009 | UI de perks e alternativas lado a lado | 004,008 | Cobertura explícita; ranks e pontos extras; M |
| CL-010 | Salvar/reabrir builds e snapshots versionados | 005,008 | Round-trip PostgreSQL, 404, recálculo explícito; M |
| CL-011 | Fluxo completo e validação com três jogadores | 006,009,010 | E2E, acessibilidade, observações de valor; S |

M0 está preparado/implementado em 001–004 e infraestrutura parcial de 005; resultados exatos em verification.md. M1 só está pronto após 006–011. Tamanhos são relativos, não promessas de duração. Caminho crítico: evidência → catálogo → motor perks → integração. Frontend pode trabalhar em estados/mock de contrato, mas nunca apresentar mock como resultado real.

## Papéis
- Coordenador: visão, contrato compartilhado, conflitos e integração; único dono dos arquivos compartilhados.
- Agente domínio/backend: motor, API e testes; não muda requisito silenciosamente.
- Agente frontend: experiência e integração; não replica regras de negócio.
- Agente revisor: inicialmente somente leitura; busca violações de invariantes, dados sem fonte e cobertura falsa. Retorna achados com arquivo, severidade e reprodução.

Dois autores em paralelo somente em diretórios independentes e após fixar contrato. Para PRs simultâneos, uma branch/checkout isolado por tarefa; não dois agentes escrevendo no mesmo arquivo. Neste bootstrap foram usados diretórios separados para backend/frontend e revisão após integração.

## Permissões
Dar aos autores leitura/escrita no projeto e execução de testes. Aprovar acesso de rede quando necessário para baixar dependências identificadas. Revisores podem começar em somente leitura. Não conceder acesso irrestrito como padrão. Um AGENTS.md orienta comportamento; não substitui o controle real de permissões do ambiente. Políticas administradas continuam prevalecendo.

Na CLI, a documentação oficial apresenta `codex --sandbox workspace-write --ask-for-approval on-request`; para revisão, `codex --sandbox read-only --ask-for-approval on-request`. São exemplos opcionais, nenhuma configuração global foi alterada. No aplicativo, mantenha as permissões do projeto e autorize ações concretas quando solicitadas. As opções disponíveis variam por ambiente.

Fontes oficiais consultadas em 2026-11-05: [permissões](https://learn.chatgpt.com/docs/agent-approvals-security), [subagentes](https://learn.chatgpt.com/docs/agent-configuration/subagents), [AGENTS.md](https://learn.chatgpt.com/docs/agent-configuration/agents-md).

## Prompt pronto — próxima tarefa
> Implemente CL-006 do ChromeLab. Leia AGENTS.md e docs/01–05. Trabalhe somente no registro de evidências e fixtures. Confira G04–G07 em fonte primária ou evidência do jogo fornecida pelo jogador. Não marque regra como validada com base apenas em snippets ou testes do próprio motor. Não altere o motor silenciosamente. Entregue evidências, diferenças encontradas e cenários de regressão. Se precisar interação do jogador com o jogo, prepare primeiro um roteiro curto e exato.

## Prompt pronto — implementação
> Implemente apenas CL-008, após CL-006 e CL-007 concluídas. Primeiro atualize contrato e exemplos de aceitação. Use um subagente somente para revisão de leitura quando a implementação estiver pronta. Não modifique frontend nem catálogo para acomodar falhas. Teste budgets, ranks, ciclos, refund de dependentes e determinismo. Entregue diff pequeno, comandos executados e limitações; sem merge ou deploy.

## Template de PR
Título: `CL-00X: comportamento concreto`.
Descrição: problema e resultado; critério AC correspondente; como validar; fontes de regras alteradas; riscos/limites. Sem checklist genérico. Meta orientativa de 200–400 linhas úteis por PR, excluindo lockfile; separar refatoração de comportamento. Não dividir artificialmente uma mudança que precise ser atômica para manter contrato e consumidor coerentes.
