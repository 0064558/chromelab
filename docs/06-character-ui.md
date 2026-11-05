# Incremento de UI — painel de personagem

Pedido: aproximar UX/UI da interface de personagem e do planner oficial. Nenhuma mudança no contrato ou nas regras.

Aceite:
- Painéis radiais com posição, cores, tipografia e botões inspirados na referência observada.
- Alternar edição entre atributos atuais e mínimos desejados sem perder valores.
- +/− edita apenas o conjunto ativo, respeitando os limites de entrada já existentes; orçamento continua validado na API.
- Rótulos de números e botões acessíveis; teclado e mobile preservados.
- Erros, reset consentido, cálculo real e descarte de resposta obsoleta continuam funcionando.
- Perks/Relic fora da cobertura continuam explícitos.
- Desktop compacto: reduzir cabeçalho, lateral e painel radial; em janelas com até 820px de altura, aplicar uma escala menor ao painel. Não ampliar automaticamente os atributos só porque a janela é larga. Preservar a apresentação mobile e o zoom escolhido no navegador.
- Refinamento: aumentar em 10% a escala radial anterior (.64→.704; .53→.583 em janelas baixas). Em desktop a partir de 1200px, centralizar atributos e ação principal entre colunas laterais de mesma largura; parâmetros/reset à esquerda e objetivos rápidos à direita. Níveis em duas colunas, descrições visíveis nos objetivos e adaptação sem sobreposição em tablet/mobile.

Referência e evidências: DESIGN.md. Verificar com testes UI existentes, testes de alternância/stepper e compilação; revisão visual quando navegador disponível.

Refinamento final: 7 testes de interface aprovados. Revisão visual em 1857×850 e 1366×768: painel e botão alinhados ao centro horizontal, com botão inteiro na janela (fim em 843px e 749px). Conferidos também 1200×850, 1024×768 e 390×844, sem overflow horizontal ou recorte do painel; tablet/mobile usam rolagem vertical. Captura: outputs/chromelab-balanced-desktop.png. Nenhuma alteração de regras/API.

Ajuste de proporção: conferido em 1857×850 e 1366×768 com painel completo e botão de cálculo dentro da altura da janela (barra de ações termina em 833px e 732px, respectivamente). Mobile conferido em viewport 390×844, sem overflow horizontal e com painel em lista. Captura: outputs/chromelab-compact-desktop.png. Alteração restrita a CSS; sem novas regras ou mudanças de API.

Verificado: build TypeScript/Vite aprovado; 7 testes de UI aprovados. Navegador real conferido em 1440×1000 e 390×844; mobile sem overflow horizontal (clientWidth=scrollWidth=390). Edição atual/objetivo preservou valores independentes (Corpo atual10→11; objetivo permaneceu15; voltar mostrou11). Capturas em outputs/chromelab-character-desktop.png e outputs/chromelab-character-mobile.png. Nenhuma mudança ou nova verificação do motor/API nesta tarefa de design. Layout segue os recursos e proporções da referência, com configurações específicas do ChromeLab na lateral; não é uma cópia pixel a pixel de toda a página oficial.
