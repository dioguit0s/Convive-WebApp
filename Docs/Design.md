## 1. Descrição do Logo

A logo da **Convive - Sistema de Administração de Condomínios** transmite modernidade, organização e um forte senso de comunidade.

**Elementos visuais:**

* **A Letra "C":** O elemento central é um grande "C" estilizado, formado por linhas concêntricas. Ele abraça os elementos internos, simbolizando proteção, acolhimento e o ecossistema do condomínio.
* **Imóveis e Natureza:** Dentro do "C", há ilustrações em *line art* (arte em linha) de edifícios/casas e uma pequena árvore, representando claramente o setor imobiliário, habitacional e a preocupação com o bem-estar e o meio ambiente.
* **Figuras Humanas:** Conectadas à parte inferior direita do "C", há duas figuras humanas estilizadas e sorridentes segurando um coração laranja. Isso reforça a ideia de "convivência", harmonia, empatia e foco no lado humano da administração.
* **Estilo Geral:** O design utiliza linhas contínuas com pontas arredondadas (*rounded caps*), criando uma estética amigável, acessível e limpa.

***

## 2. Roteiro de Design (Guia de Estilo Visual)

Para criar um sistema com interface (UI) consistente com a marca, siga as diretrizes abaixo:

### Paleta de Cores

As cores transmitem tranquilidade (azuis/verdes) e calor humano (laranja). *Nota: Os códigos Hexadecimais são aproximações baseadas na imagem para uso direto em CSS/Figma.*

* **Cor Primária (Marca e Títulos):**
  * **Azul Petróleo Escuro:**`#1A6E82` (Usado no texto "Convive" e nos contornos principais). Traz seriedade e confiança.
* **Cores Secundárias (Apoio e UI):**
  * **Azul Claro/Ciano:**`#5AB3C4` (Usado nas linhas internas e na figura humana da esquerda). Traz leveza e modernidade.
  * **Verde Suave:**`#48A682` (Usado na árvore e na figura da direita). Representa crescimento e bem-estar.
* **Cor de Destaque (Accent/Ações secundárias):**
  * **Laranja/Coral:**`#F09756` (Usado no coração). Excelente para botões de alerta suave, ícones de notificação ou pequenos detalhes que precisam chamar a atenção (sem usar vermelho agressivo).
* **Cores Neutras (Fundos e Textos de leitura):**
  * **Fundo Principal:**`#FFFFFF` (Branco puro para áreas de conteúdo).
  * **Fundo Secundário:**`#F5F8F9` (Um cinza muito leve, quase azulado, para destacar cards ou menus laterais).
  * **Texto de Corpo (Body):**`#334145` (Um cinza escuro azulado, melhor para leitura longa do que o preto puro).

### Tipografia

O logo utiliza uma fonte sem serifa (sans-serif) com cantos levemente arredondados, passando uma sensação amigável. Para o sistema, recomenda-se o uso de fontes gratuitas do Google Fonts que conversem com essa estética.

* **Fonte Principal (Títulos, Cabeçalhos e Botões):**
  * **Recomendação:**`Nunito` ou `Quicksand`.
  * **Por quê:** Possuem o formato arredondado que combina perfeitamente com a tipografia da palavra "Convive".
* **Fonte Secundária (Textos longos, parágrafos, tabelas de dados):**
  * **Recomendação:**`Inter`, `Roboto` ou `Montserrat`.
  * **Por quê:** São altamente legíveis em tamanhos pequenos e trazem o tom corporativo necessário para um sistema de administração.

### Elementos Gráficos e UI (Interface do Usuário)

Para que os botões, modais e cards do sistema pareçam pertencer à marca "Convive", siga estas regras de forma:

1. **Bordas Arredondadas (Border-Radius):**
   * Evite cantos 100% retos (quadrados).
   * Use um *border-radius* médio a alto. Por exemplo: `8px` para cards de informação e painéis, e `50px` (totalmente arredondado/pílula) para botões de ação principal.
2. **Ícones do Sistema:**
   * Utilize bibliotecas de ícones no estilo **Line Art** (apenas contornos, sem preenchimento sólido, exceto quando ativos).
   * A espessura da linha dos ícones deve ser consistente (ex: 2px) e as pontas das linhas devem ser arredondadas (*stroke-linecap: round*), imitando o desenho do logo. (Bibliotecas recomendadas: *Feather Icons* ou *Phosphor Icons*).
3. **Sombras (Drop Shadows):**
   * Mantenha o sistema leve (Clean). Use sombras muito suaves e difusas apenas para separar elementos sobrepostos (como modais ou menus suspensos), preferencialmente com tons de azul marinho translúcido em vez de preto puro (ex: `rgba(26, 110, 130, 0.08)`).
4. **Espaçamento (Whitespace):**
   * O logo respira muito bem. O sistema também deve ser arejado. Evite telas superlotadas de informações; use margens e preenchimentos generosos (padrões múltiplos de 8px, como 16px, 24px, 32px) para separar seções do painel administrativo.
