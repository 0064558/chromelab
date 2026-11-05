# ChromeLab

Leia docs/01-vision-spec.md, docs/02-rules-evidence.md e o contrato antes de editar.

- Faça um incremento do backlog por vez. Atualize spec e aceitação antes de mudar regra ou API.
- Java é a única fonte de verdade das regras. Sem LLM em runtime, duplicação do motor na UI ou dados fictícios apresentados como confirmados.
- Não promover rulesets PENDING/EXPERIMENTAL sem evidência e revisão registradas.
- Preserve determinismo, budgets, pré-requisitos, DLC e consentimento explícito para simular reset.
- Não exibir viabilidade de equipamento/perk quando apenas atributos foram avaliados.
- Trabalhe dentro do diretório atribuído; coordene arquivos compartilhados. Use subagente para revisão quando solicitado na tarefa.
- Rode testes relevantes. Relate precisamente testes não executados, falhas de ambiente e cobertura faltante.
- Não adicionar serviços/credenciais ou publicar, fazer merge ou alterar saves como parte implícita de uma tarefa local.
- Banco é PostgreSQL. Migrations antigas não são reescritas após aplicação compartilhada.
- Segredos e caches ficam fora do Git; `.env.example` contém apenas exemplos de desenvolvimento.

Ver docs/05-tasks-agents.md para prompts, permissões e PRs.
