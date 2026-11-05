# ChromeLab — interface de personagem

Referência: https://www.cyberpunk.net/en/build-planner?a=33333&b=&r=&i=&t=&c=&e=
Captura: 2026-11-05. Evidências locais ignoradas no Git: `.firecrawl/planner-branding.json`, `.firecrawl/planner-screenshot.json`, `work/reference/planner.png` e CSS público em `work/reference/planner.css`.

## Observado
Fundo #07080d, ciano #29ffff, vermelho #ff5c53, amarelo #ffd83f. Fonte Blender Pro Book/Bold. Painéis de atributos angulares em disposição radial de 762×820, Reflexos acima, Corpo à esquerda, Técnica à direita, Inteligência abaixo à esquerda e Moral abaixo à direita. Centro com nível. Controles incrementais em torno dos painéis; decoração de circuito, linhas finas e ausência de cantos arredondados. O branding automático reportou também cores do cabeçalho; o CSS e a captura do planner são a fonte de verdade para os atributos.

## Implementação ChromeLab
Reutilizar recursos públicos de fundo, painéis, ícones e fontes para atender à referência pedida, guardando origem em `frontend/public/planner-assets/SOURCES.md`. Nome ChromeLab próprio; nenhum cabeçalho de venda ou logo de produto substituindo sua identidade. Recursos continuam pertencendo aos respectivos titulares; uso público/distribuição requer avaliar licenças, que não foram verificadas nesta tarefa.

Um painel radial alterna Personagem atual/Objetivo; editar por número ou +/−. Configurações de níveis, DLC e consentimento ficam em coluna lateral. Estado selecionado ciano, erro vermelho, ação principal amarela. Resultado usa os mesmos tokens. Controles são semanticamente rotulados e teclado continua funcional. Em telas estreitas o painel radial vira lista para manter alvos de toque e números legíveis. Essa adaptação mobile é uma decisão ChromeLab, não uma medição da referência.

Refinamento de distribuição: no desktop (a partir de 1200px), duas colunas laterais iguais enquadram o painel radial e o botão principal no eixo central da tela. Parâmetros e reset à esquerda; objetivos com descrições à direita. Abaixo desse limite, o painel precede as configurações para evitar recortes. A escala radial cresceu 10% em relação à versão compacta (.704 e .583 em telas baixas); margens e cabeçalho compensam o aumento sem forçar o zoom do navegador.

Não adicionar árvore de perks falsa, Relic funcional ou regra na UI. Os mínimos continuam sendo validados exclusivamente no Java. Preservar cancelamento de requisições e descarte de resultado obsoleto. Não alegar equivalência pixel a pixel antes de comparação visual renderizada.
