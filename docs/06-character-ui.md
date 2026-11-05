# Incremento de UI — painel de personagem

Pedido: aproximar UX/UI da interface de personagem e do planner oficial. Nenhuma mudança no contrato ou nas regras.

Aceite:
- Painéis radiais com posição, cores, tipografia e botões inspirados na referência observada.
- Alternar edição entre atributos atuais e mínimos desejados sem perder valores.
- +/− edita apenas o conjunto ativo, respeitando os limites de entrada já existentes; orçamento continua validado na API.
- Rótulos de números e botões acessíveis; teclado e mobile preservados.
- Erros, reset consentido, cálculo real e descarte de resposta obsoleta continuam funcionando.
- Perks/Relic fora da cobertura continuam explícitos.

Referência e evidências: DESIGN.md. Verificar com testes UI existentes, testes de alternância/stepper e compilação; revisão visual quando navegador disponível.

Verificado: build TypeScript/Vite aprovado; 7 testes de UI aprovados. Navegador real conferido em 1440×1000 e 390×844; mobile sem overflow horizontal (clientWidth=scrollWidth=390). Edição atual/objetivo preservou valores independentes (Corpo atual10→11; objetivo permaneceu15; voltar mostrou11). Capturas em outputs/chromelab-character-desktop.png e outputs/chromelab-character-mobile.png. Nenhuma mudança ou nova verificação do motor/API nesta tarefa de design. Layout segue os recursos e proporções da referência, com configurações específicas do ChromeLab na lateral; não é uma cópia pixel a pixel de toda a página oficial.
