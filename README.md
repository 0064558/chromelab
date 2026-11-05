# ChromeLab

Build Rescue + Smart Progression para Cyberpunk 2077. Este bootstrap entrega o pacote SDD e a primeira fatia funcional: planejar atributos a partir do personagem atual, preservar investimentos e simular reset somente quando autorizado.

**Estado: prévia experimental M0.** Regras numéricas aguardam conferência no jogo. Perks, equipamentos e persistência de builds são próximos incrementos, não funcionalidades concluídas. Nenhuma IA é usada no runtime.

## Comece aqui
- [Visão, escopo e especificação](docs/01-vision-spec.md)
- [Regras e evidências, com pendências explícitas](docs/02-rules-evidence.md)
- [Critérios de aceitação](docs/03-acceptance.md)
- [Arquitetura, decisões e modelo de dados](docs/04-architecture-data.md)
- [Issues, fluxo com agentes, permissões e prompts](docs/05-tasks-agents.md)
- [Contrato OpenAPI](docs/openapi.yaml) e [contrato textual inicial](docs/contract-first.md)
- [Verificação e limitações](docs/verification.md)

## Executar localmente
Pré-requisitos: Java 21+ e Maven; Node 24 com npm. PostgreSQL é opcional nesta fatia.

Terminal 1, na pasta backend:
```powershell
cd backend
mvn test
mvn spring-boot:run
```

Terminal 2, na pasta frontend:
```powershell
cd frontend
npm ci
npm test
npm run build
npm run dev
```
Abra http://127.0.0.1:5173. O Vite encaminha `/api` ao Spring na porta8080. Perfil padrão calcula sem banco. Para parar, Ctrl+C em cada terminal. Dependências exigem rede no primeiro uso; em ambiente com cache restrito, informe `'-Dmaven.repo.local=<pasta-gravavel>'` ao Maven.

Se a porta8080 estiver ocupada, defina `$env:SERVER_PORT='18080'` no terminal do backend e `$env:API_PROXY_TARGET='http://127.0.0.1:18080'` no terminal do frontend antes de iniciar cada processo.

Exemplo inicial: nível23, atributos 10/10/9/8/7, alvo40 e mínimos15/20/9/3/3 → 15 investimentos, objetivo no nível38, sem reset, sob as regras experimentais cadastradas.

## PostgreSQL preparado
Com Docker Desktop iniciado, a partir da raiz:
```powershell
docker compose up -d postgres
$env:DB_URL = 'jdbc:postgresql://127.0.0.1:55432/chromelab'
$env:DB_USER = 'chromelab'
$env:DB_PASSWORD = 'chromelab_dev'
cd backend
mvn spring-boot:run '-Dspring-boot.run.profiles=postgres'
```
Encerre o backend padrão antes de iniciar outro na mesma porta. O perfil aplica Flyway e prepara tabelas de rulesets, builds e snapshots; ainda não há endpoints para salvar/reabrir builds. Senha do Compose é apenas para desenvolvimento local. `docker compose stop` preserva o volume. GET `/api/v1/health` indica processo ativo, não prontidão de banco.

O PostgreSQL do Compose usa a porta local55432. Para outro valor, defina POSTGRES_PORT no Compose e DB_URL correspondente no backend.

Defina essas variáveis no mesmo terminal do Maven; elas não são transferidas automaticamente para outra janela. Se ocorrer `password authentication failed`, confira as três variáveis para evitar conectar a outro banco ou usar uma senha antiga.

## Próximo incremento
CL-006: validar as regras numéricas e fixar patch/plataforma suportados; depois CL-007 e CL-008: catálogo pequeno de perks com fontes e planejamento de pré-requisitos. O backlog separa a entrega M0 do MVP completo M1.

Nenhum remoto, PR ou implantação foi criado. AGENTS.md e o roteiro de PRs estão prontos para uso no repositório que você escolher.
