# Convive - Conceito atualizado

Sistema de administração e convivência condominial.

Documento atualizado com base nos fluxos e na modelagem descritos em
`Docs/Diagramas Convive.pdf`.

## 1. Visão geral

O Convive organiza processos do condomínio em três frentes principais:

- **Acesso e gestão:** autenticação, cadastro e manutenção de usuários,
  visualização de indicadores gerenciais e geração de relatórios.
- **Espaços compartilhados:** solicitação, validação, acompanhamento,
  aprovação ou cancelamento de reservas.
- **Comunidade e suporte:** publicação de comunicados, criação e inscrição em
  eventos, registro e triagem de ocorrências.

O sistema é estruturado em camadas de interface web, controladores, serviços de
negócio e persistência. Os serviços concentram validações como duplicidade de
cadastro, permissões, disponibilidade de agenda, inadimplência, capacidade de
eventos e priorização de ocorrências.

## 2. Atores e perfis de acesso

### Morador

Usuário final do condomínio. Pode:

- realizar login;
- solicitar reserva de espaço compartilhado;
- adicionar participantes a uma reserva, quando aplicável;
- visualizar a própria agenda de reservas;
- inscrever-se em eventos;
- registrar ocorrências com descrição e evidências.

### Moderador / Porteiro

Perfil operacional responsável por rotinas administrativas. Pode:

- realizar login;
- gerenciar usuários e moradores;
- visualizar dashboard gerencial;
- aprovar ou cancelar reservas;
- publicar comunicados;
- criar eventos;
- avaliar e triar ocorrências.

### Administrador / Síndico

Especialização do moderador com acesso gerencial ampliado. Pode:

- realizar todas as ações do moderador;
- gerar relatórios;
- administrar cadastros, indicadores e regras operacionais do condomínio.

## 3. Modelagem de entidades

### Usuario

Entidade base dos usuários do sistema.

- `id: UUID`
- `nome: String`
- `email: String`
- `senhaHash: String`
- `status: String`
- `isInadimplente: boolean`

### Morador

Especialização de `Usuario` para residentes.

- `apartamento: int`

### Moderador

Especialização de `Usuario` para operadores do condomínio.

### Administrador

Especialização de `Moderador` para o perfil de síndico/administrador.

### Reserva

Representa a solicitação ou confirmação de uso de um espaço compartilhado.

- `id: int`
- `inicio: LocalDate`
- `fim: LocalDate`
- `status: StatusReserva`
- `reservadoPor: Usuario`

Relacionamento: um `Usuario` pode possuir várias `Reserva`.

### Ocorrencia

Registra uma solicitação, problema ou evidência enviada por morador para análise
da moderação.

- `id: UUID`
- `descricao: String`
- `prioridade: Prioridade`
- `status: StatusOcorrencia`
- `morador: Morador`

Relacionamento: um `Morador` pode registrar várias `Ocorrencia`.

### Comunicado

Conteúdo publicado para a comunidade por um moderador.

- `id: int`
- `titulo: String`
- `conteudo: String`
- `publicadoEm: LocalDate`
- `moderador: Moderador`

Relacionamento: um `Moderador` pode publicar vários `Comunicado`.

### Notificacao

Mensagem enviada pelo sistema ou por usuário autorizado para comunicar ações e
resultados dos fluxos.

- `id: UUID`
- `mensagem: String`
- `enviadaEm: LocalDate`
- `enviadaPor: Usuario`

Relacionamento: um `Usuario` pode originar várias `Notificacao`.

### Enumerações

`Prioridade`:

- `Alta`
- `Media`
- `Baixa`

`StatusReserva`:

- `Pendente`
- `Aprovado`
- `Reprovado`

`StatusOcorrencia`:

- reservado para representar os estados de triagem e tratamento de uma
  ocorrência, como registrada, em análise, resolvida ou rejeitada.

## 4. Fluxos principais

### 4.1 Cadastro de usuário

1. O moderador preenche o formulário de cadastro na interface web.
2. A interface envia os dados ao controlador de autenticação.
3. O controlador aciona o serviço de usuários.
4. O serviço consulta o banco de dados para verificar se já existe usuário com o
   mesmo e-mail.
5. Se o e-mail ainda não estiver cadastrado, o usuário é salvo e o cadastro é
   aprovado.
6. Se o e-mail já existir, o serviço retorna erro de duplicidade.
7. A interface exibe mensagem de sucesso ou erro ao moderador.

Regra central: e-mail deve ser único para evitar duplicidade de acesso.

### 4.2 Publicação de comunicado

1. O moderador escreve o comunicado e solicita a publicação.
2. A interface envia os dados ao controlador de comunicados.
3. O serviço de comunicados valida se o moderador possui permissão.
4. Com permissão válida, o comunicado é persistido.
5. Para cada morador ativo, o serviço aciona notificações para avisar sobre a
   publicação.
6. Sem permissão, a publicação é rejeitada.
7. A interface informa confirmação ou erro ao moderador.

Regra central: somente perfis autorizados podem publicar comunicados para a
comunidade.

### 4.3 Inscrição de morador em eventos

1. O morador seleciona um evento e confirma a inscrição.
2. A interface envia `eventoId` e `usuarioId` ao controlador de eventos.
3. O serviço de eventos busca o evento com suas inscrições atuais.
4. Se houver vagas, a inscrição é salva e uma confirmação é enviada por
   notificação.
5. Se o evento estiver lotado, a inscrição é recusada.
6. A interface apresenta o status da inscrição ao morador.

Regra central: a inscrição depende da capacidade disponível do evento.

### 4.4 Reserva de espaços compartilhados

1. O morador informa espaço, data e horário desejados.
2. A interface envia os dados ao controlador de reservas.
3. O serviço de reservas consulta o serviço de agenda para verificar a
   disponibilidade.
4. O serviço de agenda consulta reservas existentes no período informado.
5. Se o espaço estiver disponível e as demais regras forem atendidas, a reserva
   é salva e o morador recebe notificação de confirmação.
6. Se o espaço estiver indisponível, o serviço retorna conflito de horário.
7. A interface exibe confirmação ou horários indisponíveis ao morador.

Regras centrais:

- verificar inadimplência do solicitante antes de concluir a reserva;
- prevenir conflito de agenda para o mesmo espaço e período;
- permitir inclusão de participantes quando o fluxo exigir;
- manter agenda própria do morador;
- submeter aprovação ou cancelamento de reserva ao moderador quando necessário.

### 4.5 Registro e triagem de ocorrências

1. O morador descreve a ocorrência e envia evidências.
2. A interface encaminha dados e anexos ao controlador de ocorrências.
3. O serviço de ocorrências registra a ocorrência no banco de dados.
4. O serviço de moderação classifica ou sugere a prioridade.
5. Para prioridade alta, o serviço de notificações alerta o moderador.
6. Para prioridade normal, a ocorrência é registrada na fila de triagem.
7. O morador recebe protocolo e status inicial.
8. Após notificação, o moderador pode consultar os detalhes da ocorrência para
   análise.

Regra central: ocorrências devem gerar rastreabilidade por protocolo e fluxo de
triagem conforme prioridade.

## 5. Regras de negócio consolidadas

- **Autenticação obrigatória:** todos os perfis realizam login para acessar suas
  funcionalidades.
- **Controle de permissão:** ações administrativas, comunicados, eventos,
  reservas e relatórios devem respeitar o perfil do usuário.
- **Cadastro único por e-mail:** o serviço de usuários deve impedir cadastros
  duplicados.
- **Inadimplência:** moradores inadimplentes não devem concluir reservas de
  espaços compartilhados.
- **Agenda de reservas:** reservas devem ser bloqueadas quando houver conflito
  de espaço e período.
- **Status de reserva:** reservas usam os estados `Pendente`, `Aprovado` e
  `Reprovado`.
- **Eventos com capacidade:** inscrições só devem ser confirmadas quando houver
  vagas.
- **Notificações:** comunicados, reservas, inscrições e ocorrências devem gerar
  retorno visível ao usuário e, quando necessário, alerta à moderação.
- **Triagem de ocorrências:** prioridade alta exige alerta imediato; demais
  prioridades entram em fluxo normal de análise.

## 6. Componentes de aplicação previstos

- `AuthController`: recebe autenticação e cadastro de usuários.
- `UsuarioService`: valida duplicidade, cria usuários e centraliza regras de
  cadastro.
- `ComunicadoController` e `ComunicadoService`: validam permissão, publicam
  comunicados e disparam notificações.
- `EventoController` e `EventoService`: controlam criação de eventos e
  inscrições conforme disponibilidade de vagas.
- `ReservaController` e `ReservaService`: processam solicitações de reserva e
  aplicam regras de negócio.
- `AgendaService`: consulta disponibilidade de espaços e conflitos de período.
- `OcorrenciaController` e `OcorrenciaService`: registram ocorrências,
  retornam protocolo e coordenam triagem.
- `ModeracaoService`: sugere ou define prioridade das ocorrências.
- `NotificacaoService`: concentra envio de confirmações, alertas e mensagens de
  status.

## 7. Dashboard e relatórios

O dashboard gerencial deve apoiar moderadores e administradores com visão
operacional do condomínio. Indicadores sugeridos:

- total de moradores cadastrados;
- usuários ativos e inativos;
- reservas pendentes de aprovação;
- próximas reservas;
- ocorrências abertas por prioridade;
- eventos com inscrições e capacidade;
- comunicados recentes.

Relatórios são atribuição do administrador/síndico e devem consolidar
informações de usuários, reservas, ocorrências, comunicados e eventos.

## 8. Testes recomendados

Priorizar testes unitários da camada de serviços e testes de fluxo para os
casos de maior risco:

- impedir cadastro com e-mail duplicado;
- impedir reserva de morador inadimplente;
- impedir reserva com conflito de agenda;
- confirmar inscrição apenas quando houver vaga;
- rejeitar publicação de comunicado sem permissão;
- notificar moradores após publicação de comunicado;
- gerar alerta para ocorrência de prioridade alta;
- retornar protocolo e status inicial ao registrar ocorrência.

## 9. Observações de evolução

O conceito antigo tratava `Apartamento` e `AreaComum` como entidades centrais.
Na modelagem atual dos diagramas, o apartamento é atributo de `Morador`, e a
reserva é associada ao usuário responsável. O fluxo de reserva ainda depende do
espaço escolhido, portanto a persistência explícita de espaços compartilhados
pode ser adicionada futuramente se o domínio exigir cadastro, capacidade,
ativação ou manutenção individual desses espaços.
