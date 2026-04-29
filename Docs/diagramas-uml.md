# Diagramas UML - Convive

## Diagrama de Classes

```mermaid
classDiagram
    class Usuario {
        +UUID id
        +String nome
        +String email
        +String senhaHash
        +StatusUsuario status
        +atualizarPerfil(dados)
        +autenticar(senha)
    }

    class Morador {
        +int apartamento
        +solicitarReserva(espaco, periodo)
        +registrarOcorrencia(descricao)
        +inscreverEmEvento(evento)
    }

    class Moderador {
        +criarComunicado(titulo, conteudo)
        +criarEvento(dadosEvento)
        +avaliarOcorrencia(ocorrencia)
    }

    class Administrador {
        +gerenciarUsuario(usuario)
        +gerarRelatorio(filtros)
    }

    class Comunicado {
        +UUID id
        +String titulo
        +String conteudo
        +DateTime publicadoEm
        +publicar()
    }

    class Evento {
        +UUID id
        +String titulo
        +DateTime dataHora
        +int capacidade
        +StatusEvento status
        +abrirInscricoes()
        +inscrever(usuario)
        +cancelar()
    }

    class InscricaoEvento {
        +UUID id
        +DateTime criadaEm
        +StatusInscricao status
        +confirmar()
        +cancelar()
    }

    class EspacoCompartilhado {
        +UUID id
        +String nome
        +int capacidade
        +boolean ativo
        +verificarDisponibilidade(data, horaInicio, horaFim)
    }

    class Reserva {
        +UUID id
        +DateTime inicio
        +DateTime fim
        +StatusReserva status
        +confirmar()
        +cancelar()
    }

    class Ocorrencia {
        +UUID id
        +String descricao
        +Prioridade prioridade
        +StatusOcorrencia status
        +registrar()
        +atualizarStatus(status)
    }

    class Notificacao {
        +UUID id
        +String mensagem
        +DateTime enviadaEm
        +enviar(usuario)
    }

    Usuario <|-- Morador
    Usuario <|-- Moderador
    Moderador <|-- Administrador

    Moderador "1" --> "0..*" Comunicado : publica
    Moderador "1" --> "0..*" Evento : organiza
    Evento "1" --> "0..*" InscricaoEvento : possui
    Morador "1" --> "0..*" InscricaoEvento : realiza
    Morador "1" --> "0..*" Reserva : solicita
    EspacoCompartilhado "1" --> "0..*" Reserva : recebe
    Morador "1" --> "0..*" Ocorrencia : registra
    Moderador "1" --> "0..*" Ocorrencia : avalia
    Usuario "1" --> "0..*" Notificacao : recebe
```

## Diagramas de Sequência

### 1. Cadastro e autenticação de usuário

```mermaid
sequenceDiagram
    autonumber
    actor Visitante
    participant WebApp as Interface Web
    participant AuthController as Controlador de Autenticação
    participant UsuarioService as Serviço de Usuários
    participant Banco as Banco de Dados

    Note over Visitante,Banco: Início da interação
    Visitante->>WebApp: Preenche formulário de cadastro
    activate WebApp
    WebApp->>AuthController: enviarCadastro(dados)
    activate AuthController
    AuthController->>UsuarioService: cadastrarUsuario(dados)
    activate UsuarioService
    UsuarioService->>Banco: buscarUsuarioPorEmail(email)
    activate Banco
    Banco-->>UsuarioService: usuário existente ou vazio
    deactivate Banco

    alt E-mail ainda não cadastrado
        UsuarioService->>Banco: salvarUsuario(usuario)
        activate Banco
        Banco-->>UsuarioService: usuário criado
        deactivate Banco
        UsuarioService-->>AuthController: cadastro aprovado
    else E-mail já cadastrado
        UsuarioService-->>AuthController: erro de duplicidade
    end

    deactivate UsuarioService
    AuthController-->>WebApp: resultadoCadastro
    deactivate AuthController
    WebApp-->>Visitante: Exibe sucesso ou mensagem de erro
    deactivate WebApp
    Note over Visitante,Banco: Fim da interação
```

### 2. Publicação de comunicado para a comunidade

```mermaid
sequenceDiagram
    autonumber
    actor Moderador
    participant WebApp as Interface Web
    participant ComunicadoController as Controlador de Comunicados
    participant ComunicadoService as Serviço de Comunicados
    participant Banco as Banco de Dados
    participant NotificacaoService as Serviço de Notificações

    Note over Moderador,NotificacaoService: Início da interação
    Moderador->>WebApp: Escreve comunicado e solicita publicação
    activate WebApp
    WebApp->>ComunicadoController: publicarComunicado(dados)
    activate ComunicadoController
    ComunicadoController->>ComunicadoService: validarEPublicar(dados, moderador)
    activate ComunicadoService

    alt Moderador possui permissão
        ComunicadoService->>Banco: salvarComunicado(comunicado)
        activate Banco
        Banco-->>ComunicadoService: comunicado salvo
        deactivate Banco

        loop Para cada morador ativo
            ComunicadoService->>NotificacaoService: notificarNovoComunicado(morador, comunicado)
            activate NotificacaoService
            NotificacaoService-->>ComunicadoService: notificação registrada
            deactivate NotificacaoService
        end

        ComunicadoService-->>ComunicadoController: publicação confirmada
    else Moderador sem permissão
        ComunicadoService-->>ComunicadoController: publicação recusada
    end

    deactivate ComunicadoService
    ComunicadoController-->>WebApp: resultadoPublicacao
    deactivate ComunicadoController
    WebApp-->>Moderador: Exibe confirmação ou erro
    deactivate WebApp
    Note over Moderador,NotificacaoService: Fim da interação
```

### 3. Inscrição de morador em evento

```mermaid
sequenceDiagram
    autonumber
    actor Morador
    participant WebApp as Interface Web
    participant EventoController as Controlador de Eventos
    participant EventoService as Serviço de Eventos
    participant Banco as Banco de Dados
    participant NotificacaoService as Serviço de Notificações

    Note over Morador,NotificacaoService: Início da interação
    Morador->>WebApp: Seleciona evento e confirma inscrição
    activate WebApp
    WebApp->>EventoController: inscrever(eventoId, usuarioId)
    activate EventoController
    EventoController->>EventoService: solicitarInscricao(eventoId, usuarioId)
    activate EventoService
    EventoService->>Banco: buscarEventoComInscricoes(eventoId)
    activate Banco
    Banco-->>EventoService: dados do evento e inscrições
    deactivate Banco

    alt Evento possui vagas
        EventoService->>Banco: salvarInscricao(eventoId, usuarioId)
        activate Banco
        Banco-->>EventoService: inscrição criada
        deactivate Banco
        EventoService->>NotificacaoService: enviarConfirmacaoInscricao(usuarioId, eventoId)
        activate NotificacaoService
        NotificacaoService-->>EventoService: confirmação enviada
        deactivate NotificacaoService
        EventoService-->>EventoController: inscrição confirmada
    else Evento lotado
        EventoService-->>EventoController: inscrição recusada por falta de vagas
    end

    deactivate EventoService
    EventoController-->>WebApp: resultadoInscricao
    deactivate EventoController
    WebApp-->>Morador: Exibe status da inscrição
    deactivate WebApp
    Note over Morador,NotificacaoService: Fim da interação
```

### 4. Reserva de espaço compartilhado

```mermaid
sequenceDiagram
    autonumber
    actor Morador
    participant WebApp as Interface Web
    participant ReservaController as Controlador de Reservas
    participant ReservaService as Serviço de Reservas
    participant Banco as Banco de Dados
    participant AgendaService as Serviço de Agenda
    participant NotificacaoService as Serviço de Notificações

    Note over Morador,NotificacaoService: Início da interação
    Morador->>WebApp: Informa espaço, data e horário desejados
    activate WebApp
    WebApp->>ReservaController: solicitarReserva(dadosReserva)
    activate ReservaController
    ReservaController->>ReservaService: criarReserva(dadosReserva, usuarioId)
    activate ReservaService
    ReservaService->>AgendaService: verificarDisponibilidade(espacoId, periodo)
    activate AgendaService
    AgendaService->>Banco: consultarReservasNoPeriodo(espacoId, periodo)
    activate Banco
    Banco-->>AgendaService: reservas encontradas
    deactivate Banco
    AgendaService-->>ReservaService: disponibilidade
    deactivate AgendaService

    alt Espaço disponível
        ReservaService->>Banco: salvarReserva(reserva)
        activate Banco
        Banco-->>ReservaService: reserva confirmada
        deactivate Banco
        ReservaService->>NotificacaoService: enviarConfirmacaoReserva(usuarioId, reserva)
        activate NotificacaoService
        NotificacaoService-->>ReservaService: notificação enviada
        deactivate NotificacaoService
        ReservaService-->>ReservaController: reserva criada
    else Espaço indisponível
        ReservaService-->>ReservaController: conflito de horário
    end

    deactivate ReservaService
    ReservaController-->>WebApp: resultadoReserva
    deactivate ReservaController
    WebApp-->>Morador: Exibe confirmação ou horários indisponíveis
    deactivate WebApp
    Note over Morador,NotificacaoService: Fim da interação
```

### 5. Registro e triagem de ocorrência

```mermaid
sequenceDiagram
    autonumber
    actor Morador
    participant WebApp as Interface Web
    participant OcorrenciaController as Controlador de Ocorrências
    participant OcorrenciaService as Serviço de Ocorrências
    participant Banco as Banco de Dados
    participant ModeracaoService as Serviço de Moderação
    participant NotificacaoService as Serviço de Notificações
    actor Moderador

    Note over Morador,Moderador: Início da interação
    Morador->>WebApp: Descreve ocorrência e envia evidências
    activate WebApp
    WebApp->>OcorrenciaController: registrarOcorrencia(dados, anexos)
    activate OcorrenciaController
    OcorrenciaController->>OcorrenciaService: criarOcorrencia(dados, usuarioId)
    activate OcorrenciaService
    OcorrenciaService->>Banco: salvarOcorrencia(ocorrencia)
    activate Banco
    Banco-->>OcorrenciaService: ocorrência registrada
    deactivate Banco

    OcorrenciaService->>ModeracaoService: classificarPrioridade(ocorrencia)
    activate ModeracaoService
    ModeracaoService-->>OcorrenciaService: prioridade sugerida
    deactivate ModeracaoService

    alt Prioridade alta
        OcorrenciaService->>NotificacaoService: alertarModeradores(ocorrencia)
        activate NotificacaoService
        NotificacaoService-->>OcorrenciaService: alerta enviado
        deactivate NotificacaoService
    else Prioridade normal
        OcorrenciaService->>NotificacaoService: registrarNaFilaDeTriagem(ocorrencia)
        activate NotificacaoService
        NotificacaoService-->>OcorrenciaService: triagem agendada
        deactivate NotificacaoService
    end

    OcorrenciaService-->>OcorrenciaController: protocolo da ocorrência
    deactivate OcorrenciaService
    OcorrenciaController-->>WebApp: resultadoRegistro
    deactivate OcorrenciaController
    WebApp-->>Morador: Exibe protocolo e status inicial
    deactivate WebApp

    opt Moderador abre a ocorrência após notificação
        Moderador->>WebApp: Consulta detalhes da ocorrência
        activate WebApp
        WebApp-->>Moderador: Exibe dados para análise
        deactivate WebApp
    end

    Note over Morador,Moderador: Fim da interação
```
