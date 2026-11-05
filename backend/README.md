# ChromeLab API — incremento 01

Java 21+ e Maven 3.6.3+. Spring Boot 3.5.16, escolhido na linha 3.5 estável, documentada como compatível até Java 25:
https://docs.spring.io/spring-boot/3.5/system-requirements.html

```powershell
mvn test
mvn spring-boot:run
```

Servidor local: `http://127.0.0.1:8080`; `GET /api/v1/health` é liveness. O frontend deve usar proxy para `/api`; não é necessário abrir CORS. O profile padrão funciona sem PostgreSQL e não persiste planos.

Com o PostgreSQL do Compose iniciado, configure as variáveis **no mesmo terminal** em que executará o Maven. Elas precisam ser definidas novamente ao abrir outro terminal. Para o banco de desenvolvimento deste projeto:

```powershell
$env:DB_URL = 'jdbc:postgresql://127.0.0.1:55432/chromelab'
$env:DB_USER = 'chromelab'
$env:DB_PASSWORD = 'chromelab_dev'
mvn spring-boot:run '-Dspring-boot.run.profiles=postgres'
```

`password authentication failed for user "chromelab"` significa que o banco recusou a autenticação. Confira endereço/porta, usuário e senha acima; uma `DB_URL` antiga pode apontar para outro PostgreSQL. A senha mostrada é exclusiva do Compose local. Para outro banco, use as credenciais dele. Não é necessário apagar volumes ou recriar o banco para corrigir variáveis do terminal.

Flyway aplica o esquema preparatório de rulesets, builds e snapshots. Não há CRUD de persistência neste incremento. O ruleset inserido tem status `draft`: números e fontes precisam da revisão do catálogo antes de disponibilizar como versão de produção.

`PlanEngine` é Java puro, sem IO. `PlanApiTest` valida integração HTTP sem banco; `PlanEngineTest` cobre orçamento, reset consentido, restrição do nível 1 e 500 cenários gerados com semente fixa. `PostgresConfigurationTest` verifica apenas seleção do profile; não substitui teste contra PostgreSQL real.

Em ambientes de arquivos restritos, passe `'-Dmaven.repo.local=<diretorio-gravavel>'` aos comandos Maven. Não versionar esse cache, `target/` nem credenciais.
