# Verificação — 2026-11-05

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
