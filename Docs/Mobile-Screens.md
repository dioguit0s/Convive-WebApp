# Convive Mobile — Especificação de Telas

**Objetivo deste documento:** descrever, tela a tela, o cliente mobile do Convive para servir de insumo a uma ferramenta de prototipação (Claude Design). Cada tela traz objetivo, elementos-chave, estados e navegação, para que o protótipo possa ser gerado sem ambiguidade.

**Base:** feature-parity com o Convive Web (`Docs/Documento-Projeto-Software.md`), reaproveitando o design system em `Docs/Design.md` (cores, tipografia Manrope, raios e elevações), adaptado a componentes nativos/touch (alvos de toque ≥44px, navegação por abas, gestos de swipe, bottom sheets no lugar de dropdowns/modais desktop).

---

## 1. Personas e escopo do app

| Persona | Papel | Uso do mobile |
|---|---|---|
| **Morador** | `ROLE_MORADOR` | App principal: consulta mural, reserva áreas comuns, abre ocorrências, recebe notificações/advertências push. |
| **Moderador/Síndico** | `ROLE_MODERADOR` | App "companion": triagem rápida de reservas/ocorrências, aprovação em movimento, gestão leve. Dashboard analítico denso permanece web. |
| **Visitante** | Não autenticado | Apenas fluxo de autenticação/recuperação de senha — sem cadastro público (contas são criadas pelo moderador). |

Não há alternância de papel dentro da conta: o login detecta o tipo de usuário (`Morador`/`Moderador`) e direciona para uma das duas shells de navegação abaixo. As telas de autenticação e as configurações de conta são compartilhadas.

---

## 2. Arquitetura de navegação

**Shell do Morador — Bottom Tab Bar (5 abas):**
`Início` · `Reservas` · `Ocorrências` · `Notificações` (badge de não lidos) · `Perfil`

**Shell do Moderador — Bottom Tab Bar (5 abas):**
`Painel` · `Reservas` (triagem) · `Ocorrências` (triagem) · `Moradores` · `Mais` (Áreas comuns, Comunicados, Advertências, Perfil)

Navegação secundária dentro de cada aba é feita por push (stack navigation), com botão de voltar no header. Ações destrutivas ou de criação usam **bottom sheet** ou tela modal full-screen com header "Cancelar / Salvar".

---

## 3. Fluxo de Autenticação (comum aos dois papéis)

### S-00 · Splash
- **Objetivo:** branding e checagem de sessão ativa (token/cookie salvo).
- **Elementos:** logo Convive centralizado sobre `surface` (#f8f9ff), leve animação de fade-in.
- **Estados:** verificando sessão → autenticado (vai para Home do papel correspondente) / não autenticado (vai para Login).

### S-01 · Login
- **Objetivo:** autenticar por e-mail/senha (equivalente a `/login`).
- **Elementos:** logo, campo E-mail, campo Senha (toggle mostrar/ocultar), checkbox "Lembrar-me", botão primário "Entrar" (Deep Blue, full width), link "Esqueci minha senha", rodapé com contato/suporte.
- **Estados:** vazio, erro de validação inline, erro de credenciais (banner vermelho "E-mail ou senha inválidos"), carregando (spinner no botão), conta inativa (mensagem específica).
- **Navegação:** sucesso → Home do Morador ou Painel do Moderador conforme papel; "Esqueci minha senha" → S-02.

### S-02 · Esqueci minha senha
- **Objetivo:** solicitar e-mail de recuperação (`/forgot-password`).
- **Elementos:** campo E-mail, botão "Enviar link de recuperação", link "Voltar ao login".
- **Estados:** enviado com sucesso (tela de confirmação "Verifique seu e-mail"), e-mail não encontrado (mensagem neutra por segurança — não revela se existe).

### S-03 · Redefinir senha
- **Objetivo:** definir nova senha a partir de token recebido por e-mail (`/reset-password?token=`).
- **Elementos:** campo Nova senha, campo Confirmar senha, indicador de força da senha, botão "Redefinir senha".
- **Estados:** token inválido/expirado (tela de erro com CTA "Solicitar novo link"), sucesso (redireciona ao Login com toast de confirmação).

---

## 4. Shell do Morador

### S-10 · Início / Mural (Home)
- **Objetivo:** landing pós-login; equivalente a `/morador/home`.
- **Elementos:**
  - Header com saudação ("Olá, {nome}"), avatar (abre Perfil), ícone de sino com badge de notificações não lidas.
  - Card de alerta se `isInadimplente = true` (banner âmbar/vermelho: "Existe pendência financeira — novas reservas estão bloqueadas").
  - Seção "Comunicados recentes": lista de 3 cards (título, tipo — Obras/Reunião/Eventos/Geral —, data de publicação, thumbnail se `urlImagem`), com "Ver todos →".
  - Seção "Minhas próximas reservas": até 3 itens compactos (área, data/horário, status chip).
  - Ações rápidas (grid de atalhos): "Nova reserva", "Nova ocorrência".
- **Estados:** vazio (sem comunicados/reservas → ilustração + texto), carregando (skeleton), pull-to-refresh.
- **Navegação:** card de comunicado → S-11; "Ver todos" → S-12; card de reserva → S-15; atalhos → S-14/S-17.

### S-11 · Detalhe do Comunicado
- **Objetivo:** leitura completa de um comunicado.
- **Elementos:** imagem de capa (se houver), chip de tipo, data de publicação, nome do moderador que publicou, corpo do texto formatado.
- **Estados:** padrão único (conteúdo somente leitura).

### S-12 · Lista de Comunicados (Mural completo)
- **Objetivo:** histórico paginado (`/comunicados`, scroll infinito via endpoint `/mais`).
- **Elementos:** filtro por tipo (chips: Todos, Obras, Reunião, Eventos, Geral), lista de cards, infinite scroll com loading spinner no rodapé.
- **Estados:** vazio, fim da lista ("Não há mais comunicados").

### S-13 · Reservas — Lista (tabs internas)
- **Objetivo:** ponto de entrada da aba Reservas (`/morador/reservas`).
- **Elementos:** segmented control com 2 sub-abas:
  - **"Áreas disponíveis"**: grid/lista de `AreaComum` (nome, capacidade, status chip Ativa/Em manutenção); áreas em manutenção ficam desabilitadas para reserva.
  - **"Minhas reservas"**: lista de `Reserva` do morador, cada item com área, data/hora início-fim, status chip (Pendente=âmbar, Aprovado=verde, Reprovado=vermelho).
  - FAB "+" (Nova reserva) flutuante.
- **Estados:** vazio por sub-aba, bloqueio por inadimplência (FAB desabilitado + tooltip).
- **Navegação:** item de área → S-14; item de reserva → S-15; FAB → S-14.

### S-14 · Nova Reserva
- **Objetivo:** criar solicitação de reserva.
- **Elementos:** seletor de área comum (se não veio pré-selecionada), date picker, seletor de turno/horário (manhã/tarde/noite/integral, refletindo `inicio`/`fim`), stepper de "Convidados estimados", textarea "Observações" (até 2000 caracteres, contador), aviso inline se a janela escolhida conflita com outra reserva ("Sujeito a triagem manual"), botão "Solicitar reserva".
- **Estados:** validação de campos obrigatórios, sucesso com auto-aprovação (toast "Reserva aprovada automaticamente!"), sucesso pendente de triagem (toast "Reserva enviada para aprovação"), erro de inadimplência.

### S-15 · Detalhe da Reserva
- **Objetivo:** acompanhar status de uma reserva específica.
- **Elementos:** header com status chip grande, dados da área, data/horário, convidados estimados, observações, motivo de rejeição (se `REPROVADO`, campo `motivoRejeicao` em destaque), botão "Cancelar reserva" (somente se `PENDENTE` ou `APROVADO` e ainda não iniciada).
- **Estados:** confirmação de cancelamento via bottom sheet ("Tem certeza?").

### S-16 · Ocorrências — Lista
- **Objetivo:** listar ocorrências abertas pelo morador (`/morador/ocorrencias`, scroll infinito).
- **Elementos:** lista de cards com protocolo (`YYYY-NNNN`), título, categoria (chip: Barulho, Infraestrutura, Limpeza, Regras, Outro), status chip (Registrada, Em análise, Resolvida, Rejeitada), data de registro. Filtro por status. FAB "+" (Nova ocorrência).
- **Estados:** vazio, infinite scroll.

### S-17 · Nova Ocorrência
- **Objetivo:** registrar ocorrência/reclamação.
- **Elementos:** campo Título (200 caracteres), seletor de Categoria (define prioridade padrão automaticamente, exibida como chip informativo), textarea Descrição (10.000 caracteres), anexo de evidência (câmera/galeria → `urlEvidencia`), botão "Registrar ocorrência".
- **Estados:** upload em progresso, sucesso (tela de confirmação exibindo o protocolo gerado).

### S-18 · Detalhe da Ocorrência
- **Objetivo:** acompanhar timeline de uma ocorrência.
- **Elementos:** protocolo em destaque, chip de status, chip de prioridade, categoria, descrição original, evidência anexada (viewer de imagem), timeline vertical de status (Registrada → Em análise → Resolvida/Rejeitada), bloco "Resposta do moderador" quando preenchido.
- **Estados:** sem resposta ainda (placeholder "Aguardando análise do moderador").

### S-19 · Notificações (Central)
- **Objetivo:** lista de advertências/avisos formais recebidos do moderador (`/morador/notificacoes`, entidade `Notificacao`), equivalente a uma central de alertas.
- **Elementos:** lista de cards com título, gravidade (chip Baixa/Média/Alta com cor correspondente), data de envio, indicador "Gerou multa" quando `gerouMulta = true`, badge de não lido.
- **Estados:** vazio, infinite scroll.
- **Navegação:** item → S-20.

### S-20 · Detalhe da Notificação/Advertência
- **Objetivo:** leitura completa de uma advertência.
- **Elementos:** título, gravidade em destaque, apartamento referenciado, data da ocorrência vs. data de envio, descrição completa, aviso de multa se aplicável, nome do moderador emissor.

### S-21 · Perfil
- **Objetivo:** dados da conta (equivalente ao header/perfil do web).
- **Elementos:** avatar (toque para trocar foto → câmera/galeria, `POST /perfil/upload-foto`), nome, e-mail, apartamento, badge de status (Ativo/Inadimplente), lista de itens de menu: "Editar dados", "Alterar senha", "Notificações push" (toggle), "Sobre o condomínio", "Ajuda/Contato", "Sair".
- **Estados:** upload de foto em progresso, confirmação de logout via bottom sheet.

### S-22 · Editar Perfil
- **Objetivo:** atualizar nome/dados editáveis e trocar senha.
- **Elementos:** formulário com campos permitidos, seção separada "Alterar senha" (senha atual, nova senha, confirmação).

---

## 5. Shell do Moderador (companion mobile)

### S-30 · Painel (Dashboard)
- **Objetivo:** visão operacional resumida (`/moderador` — versão mobile simplificada do dashboard web).
- **Elementos:** cards de KPI (reservas pendentes, ocorrências abertas, moradores inadimplentes, comunicados no mês) em grid 2x2, gráfico simplificado de ocorrências por categoria (barra horizontal), lista "Ações pendentes" combinando reservas e ocorrências aguardando triagem, ordenadas por prioridade/urgência.
- **Estados:** vazio ("Tudo em dia!"), pull-to-refresh.
- **Navegação:** cada KPI leva à respectiva aba de triagem filtrada.

### S-31 · Triagem de Reservas — Lista
- **Objetivo:** fila de reservas pendentes (`/moderador/triagem-reservas`).
- **Elementos:** lista de cards com morador solicitante, apartamento, área, data/horário, convidados estimados; swipe-to-approve (verde) e swipe-to-reject (vermelho) diretamente na lista, além de toque para abrir detalhe. Filtro por área/status.
- **Estados:** vazio, indicador de conflito de agenda entre reservas próximas.

### S-32 · Detalhe da Reserva (Triagem)
- **Objetivo:** decisão individual com contexto completo.
- **Elementos:** dados completos da solicitação, histórico do morador (inadimplência, reservas anteriores), botão "Aprovar" (verde), botão "Rejeitar" (abre bottom sheet exigindo `motivoRejeicao`).
- **Estados:** confirmação de ação, sucesso com toast e retorno à lista.

### S-33 · Triagem de Ocorrências — Lista
- **Objetivo:** fila de ocorrências (`/moderador/triagem-ocorrencias`).
- **Elementos:** cards com protocolo, título, categoria, prioridade (chip colorido Alta/Média/Baixa), status, apartamento de origem. Filtros por status/categoria/prioridade.
- **Estados:** vazio, ordenação padrão por prioridade desc.

### S-34 · Detalhe da Ocorrência (Triagem)
- **Objetivo:** atualizar status e responder ao morador.
- **Elementos:** dados completos + evidência, seletor de novo status (Registrada/Em análise/Resolvida/Rejeitada), seletor de prioridade, textarea "Resposta ao morador", botão "Salvar atualização".

### S-35 · Gestão de Moradores — Lista
- **Objetivo:** consulta e administração de contas (`/moderador/moradores`).
- **Elementos:** busca por nome/apartamento, lista com avatar, nome, apartamento, badge de status (Ativo/Inadimplente), toque abre detalhe. FAB "+" (Novo morador).
- **Estados:** vazio, busca sem resultados.

### S-36 · Detalhe do Morador
- **Objetivo:** editar cadastro, alternar inadimplência, ver histórico e emitir advertência.
- **Elementos:** dados do morador (editáveis), toggle "Inadimplente", histórico resumido (reservas/ocorrências), botão "Emitir advertência" (→ S-37), botão "Excluir conta" (confirmação).

### S-37 · Nova Advertência
- **Objetivo:** registrar advertência formal a um morador (`/moderador/advertencias/nova`).
- **Elementos:** morador pré-selecionado (ou seletor), campo Título, textarea Descrição, seletor de Gravidade (Baixa/Média/Alta), data da ocorrência (date picker), toggle "Gerou multa", botão "Enviar advertência".

### S-38 · Áreas Comuns — Lista
- **Objetivo:** CRUD de áreas comuns (`/moderador/areas-comuns`).
- **Elementos:** lista com nome, capacidade, status chip (Ativa/Em manutenção), toque abre edição, FAB "+" (Nova área).
- **Estados:** vazio.

### S-39 · Nova/Editar Área Comum
- **Objetivo:** criar ou editar uma área.
- **Elementos:** campo Nome, campo Capacidade (numérico), toggle de Status (Ativa/Em manutenção), botão "Salvar".

### S-40 · Comunicados (Moderador) — Lista
- **Objetivo:** gerenciar publicações (`/comunicados`, escopo de escrita do moderador).
- **Elementos:** lista dos comunicados publicados, com ações de exclusão (swipe ou menu contextual), FAB "+" (Novo comunicado).

### S-41 · Novo Comunicado
- **Objetivo:** publicar aviso ao mural dos moradores.
- **Elementos:** campo Título, seletor de Tipo (Obras/Reunião/Eventos/Geral), textarea Conteúdo, upload de imagem opcional, botão "Publicar".

---

## 6. Componentes e telas globais

| Tela/Componente | Descrição |
|---|---|
| **Bottom Sheet de confirmação** | Padrão para ações destrutivas/irreversíveis (cancelar reserva, excluir conta, logout, rejeitar reserva). |
| **Toast/Snackbar** | Feedback de sucesso/erro no rodapé, auto-dismiss em 3s. |
| **Estado vazio (Empty State)** | Ilustração leve + texto + CTA, reutilizado em todas as listas. |
| **Estado offline** | Banner fixo no topo "Sem conexão — mostrando dados salvos", ações de escrita desabilitadas. |
| **Erro genérico (500)** | Tela full-screen com ilustração, botão "Tentar novamente". |
| **404 / Não encontrado** | Para deep links inválidos (ex.: notificação push de um item excluído). |
| **Central de push notifications** | Notificações nativas mapeadas a eventos do backend: `OcorrenciaCriadaEvent` → moderador; mudança de status de ocorrência → morador; `ReservaPendenteCriadaEvent` → moderador; `ReservaRejeitadaEvent`/aprovação → morador; nova advertência → morador; novo comunicado → morador. Toque no push faz deep link direto para a tela de detalhe (S-15, S-18, S-20, S-32, S-34, S-11). |

---

## 7. Tokens visuais para o protótipo

Reaproveitar integralmente `Docs/Design.md`:
- **Cores:** primary `#000000`/Deep Blue navigation, secondary (verde `#006d30`) para aprovações/sucesso, error `#ba1a1a` para rejeições/urgência, superfícies em tons de azul claro (`#f8f9ff`, `#eff4ff`).
- **Tipografia:** Manrope; em mobile usar `h2`/`h3` para títulos de tela (headers compactos), `body-md`/`body-sm` para conteúdo, `label-md` para chips de status (uppercase).
- **Forma:** cards e inputs com raio `0.5rem`; containers/bottom sheets com raio `1rem` (apenas cantos superiores nos sheets); status chips totalmente arredondados (`rounded-full`).
- **Navegação mobile:** bottom tab bar em superfície `surface-container-lowest` com item ativo em Deep Blue e ícones lineares 24px — mesma linguagem da navegação lateral do web, adaptada para toque.

---

## 8. Priorização (MVP mobile)

| Prioridade | Telas | Racional |
|---|---|---|
| **Must (v1)** | S-00 a S-03 (auth), S-10 a S-18 (home/reservas/ocorrências do morador), S-21/S-22 (perfil) | Replica o núcleo de valor do web para o morador, o público que mais se beneficia de mobile. |
| **Should (v1.1)** | S-19/S-20 (notificações/advertências), S-31 a S-34 (triagem do moderador em mobile) | Fecha o loop de comunicação e dá mobilidade ao síndico para aprovações rápidas. |
| **Could (v2)** | S-30 (painel com gráficos), S-35 a S-41 (gestão de moradores/áreas/comunicados no mobile) | Fluxos administrativos mais densos, hoje bem atendidos pelo web; migrar quando houver demanda de uso em campo. |
| **Won't (agora)** | Cadastro público (self-signup), multi-condomínio, portaria | Fora do escopo do backend atual (ver Roadmap em `Documento-Projeto-Software.md`). |
