# Diagramas

# Diagrama de classes completo

```mermaid
classDiagram
    class Usuario {
        <<abstract>>
        +UUID id
        +String nome
        +String email
        +String senhaHash
        +String status
        +boolean isInadimplente
        +getTipoUsuario()* String
    }

    class Morador {
        +int apartamento
    }

    class Moderador {
    }

    class Administrador {
    }

    class AreaComum {
        +UUID id
        +String nome
        +StatusArea statusArea
        +int capacidade
    }

    class Comunicado {
        +UUID id
        +String titulo
        +String conteudo
        +LocalDate publicadoEm
    }

    class Notificacao {
        +UUID id
        +String mensagem
        +LocalDate enviadaEm
    }

    class Ocorrencia {
        +UUID id
        +String descricao
        +Prioridade prioridade
        +StatusOcorrencia status
    }

    class Reserva {
        +UUID id
        +LocalDate inicio
        +LocalDate fim
        +StatusReserva status
    }

    class ContactMessageModel {
        <<DTO>>
        +String fullName
        +String email
        +String subject
        +String message
    }

    class Prioridade {
        <<enumeration>>
        ALTA
        BAIXA
        MEDIA
    }

    class StatusReserva {
        <<enumeration>>
        PENDENTE
        APROVADO
        REPROVADO
    }

    class StatusOcorrencia {
        <<enumeration>>
        REGISTRADA
        EM_ANALISE
        RESOLVIDA
        REJEITADA
    }

    class StatusArea {
        <<enumeration>>
        ATIVA
        EM_MANUTENCAO
    }

    Usuario <|-- Morador
    Usuario <|-- Moderador
    Moderador <|-- Administrador

    Moderador "1" --> "0..*" Comunicado : publicadoPor
    Morador "1" --> "0..*" Ocorrencia : feitaPor
    Usuario "1" --> "0..*" Notificacao : enviadaPor
    Usuario "1" --> "0..*" Reserva : reservadoPor
    AreaComum "1" --> "0..*" Reserva : areaReservada
```

# Diagrama de cadastro de usuário

```mermaid
sequenceDiagram
    autonumber
    actor Moderador
    participant WebApp as Interface Web
    participant AuthController as Controlador de Autenticação
    participant UsuarioService as Serviço de Usuários
    participant Banco as Banco de Dados

    Note over Moderador,Banco: Início da interação
    Moderador->>WebApp: Preenche formulário de cadastro
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
    WebApp-->>Moderador: Exibe sucesso ou mensagem de erro
    deactivate WebApp
    Note over Moderador,Banco: Fim da interação
```

# Diagrama publicação de comunicado para a comunidade

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

# Diagrama de reserva de espaços

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

# Diagrama cadastro de ocorrencias

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

# Diagrama caso de uso Ocorrências 

```mermaid
flowchart LR
    Mor((Morador))
    Mod((Moderador))

    subgraph Convive - Comunidade e Suporte
        UC12([Publicar Comunicado])
        UC13([Criar Evento])
        UC14([Inscrever-se em Evento])
        UC15([Registrar Ocorrência com Evidências])
        UC16([Avaliar e Triar Ocorrência])
    end

    Mod --> UC12
    Mod --> UC13
    Mor --> UC14
    
    Mor --> UC15
    Mod --> UC16
```

# Diagrama caso de uso Reservas

```mermaid
flowchart LR
    Mor((Morador))
    Mod((Moderador))

    subgraph Convive - Espaços Compartilhados
        UC6([Solicitar Reserva de Espaço])
        UC7([Verificar Inadimplência])
        UC8([Prevenir Conflito de Agenda])
        UC9([Adicionar Participantes])
        UC10([Visualizar Agenda Própria])
        UC11([Aprovar ou Cancelar Reserva])
    end

    Mor --> UC6
    Mor --> UC10
    
    %% Relacionamentos de Inclusão e Extensão
    UC6 -. "<<include>>" .-> UC7
    UC6 -. "<<include>>" .-> UC8
    UC6 -. "<<extend>>" .-> UC9
    
    Mod --> UC11
```

# Diagrama caso de uso Acessos

```mermaid
flowchart LR
    %% Atores
    Mor((Morador))
    Mod((Moderador / Porteiro))
    Adm((Administrador / Síndico))

    subgraph Convive - Acesso e Gestão
        UC2([Realizar Login])
        UC3([Gerenciar Usuários / Moradores])
        UC4([Visualizar Dashboard Gerencial])
        UC5([Gerar Relatórios])
    end

    Mor --> UC2
    Mod --> UC2
    Adm --> UC2

    Adm --> UC3
    Mod --> UC3
    Mod --> UC4
    Adm --> UC4
    Adm --> UC5
```
