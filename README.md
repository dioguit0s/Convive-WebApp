# 🏢 Convive - Plataforma de Gestão Condominial

[![CI](https://github.com/dioguit0s/Convive-WebApp/actions/workflows/ci.yml/badge.svg)](https://github.com/dioguit0s/Convive-WebApp/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

O **Convive** é uma alternativa **open source e self-hosted** aos sistemas de gestão condominial pagos. Roda na sua própria infraestrutura, com o código-fonte completo aberto — sem mensalidade, sem vendor lock-in.

> **Estado do projeto:** nasceu como projeto de faculdade e hoje é mantido como software livre. Cobre o essencial da gestão condominial (comunicados, reservas de áreas comuns, ocorrências, notificações); recursos como pagamentos, portaria e app mobile ainda não existem — veja [Roadmap](#-roadmap).

## 🚀 Quickstart (self-hosted)

Pré-requisito: [Docker](https://docs.docker.com/get-docker/) e Docker Compose.

```bash
git clone https://github.com/dioguit0s/Convive-WebApp.git
cd Convive-WebApp
cp .env.example .env   # edite e defina APP_REMEMBER_ME_KEY e demais variáveis
docker compose up --build
```

A aplicação sobe em `http://localhost:8085` já com dados de exemplo (moradores, áreas comuns, reservas, ocorrências e comunicados fictícios) — veja as credenciais de teste no log de inicialização do container `app`.

Para rodar localmente sem Docker (perfil de desenvolvimento com banco H2 embutido):

```bash
cd Convive
./mvnw spring-boot:run
```

## ✨ Funcionalidades

A aplicação é dividida em dois domínios principais de acesso, com separação de responsabilidades via Spring Security:

### 👤 Portal do Morador

* **Home/Mural**: visualização de comunicados importantes emitidos pela administração.
* **Gestão de Reservas**: solicitação, visualização e cancelamento de reservas de áreas comuns.
* **Ocorrências**: abertura e acompanhamento de ocorrências, reclamações ou sugestões.
* **Notificações**: central de alertas sobre mudanças de status nas solicitações.

### 🛡️ Dashboard Operacional (Moderador/Síndico)

* **Visão Geral**: dashboard com métricas e gráficos de uso do condomínio.
* **Triagem de Reservas**: aprovação, rejeição e gerenciamento de conflitos de agenda.
* **Triagem de Ocorrências**: análise, mudança de status e resolução de problemas relatados.
* **Gestão de Moradores**: controle de usuários, advertências e perfis, incluindo flag de inadimplência.
* **Comunicação**: criação e disparo de comunicados globais.

### ⚙️ Recursos de Sistema

* **Event-Driven Notifications**: *Application Events* do Spring (`OcorrenciaCriadaEvent`, `ReservaRejeitadaEvent`, etc.) para desacoplar regras de negócio do envio de e-mails.
* **Recuperação de Senha**: fluxo completo de *forgot password* com tokens seguros por e-mail.
* **CI**: pipeline via GitHub Actions rodando a suíte de testes em cada PR.

## 🆚 Convive vs. SaaS de gestão condominial

| | Convive (open source) | SaaS pago típico |
|---|---|---|
| Custo | Gratuito, você hospeda | Mensalidade por unidade/condomínio |
| Código-fonte | Aberto (MIT) | Fechado |
| Dados | Ficam na sua própria infraestrutura | Ficam com o fornecedor |
| Suporte | Comunidade / issues no GitHub | Suporte contratado |

## 🛠️ Tecnologias e Arquitetura

Padrão **MVC (Model-View-Controller)** server-side, sem SPA.

**Back-end:** Java 21, Spring Boot 3.2 (Web, Data JPA, Security, Mail, Validation), Maven.

**Front-end:** Thymeleaf (Server-Side Rendering), HTML5, CSS3, JavaScript vanilla, TailwindCSS.

**Infraestrutura:** PostgreSQL em produção (H2 embutido para desenvolvimento local), Docker/Docker Compose para self-hosting, GitHub Actions para CI.

## 📂 Estrutura do Projeto

O back-end está organizado de forma modular no pacote `Convive/src/main/java/com/EC6/Convive/`:

* `Config/`: configurações globais de segurança (`SecurityConfig`), MVC e inicialização de dados (`DataInitializer`).
* `Controller/`: rotas e fluxo das views (`Moderador`, `Morador`, `Public`).
* `Model/`: entidades de domínio (`Usuario`, `Reserva`, `Ocorrencia`, etc.).
* `Repository/`: interfaces Spring Data JPA.
* `Service/`: regras de negócio isoladas das rotas web.
* `Event/` & `Listener/`: padrão Observer para notificações assíncronas.
* `Security/`: implementações customizadas de `UserDetailsService`.

## 🗺️ Roadmap

Itens em aberto e priorização estão rastreados nas [issues do repositório](https://github.com/dioguit0s/Convive-WebApp/issues).
## 🤝 Contribuindo

Issues e PRs são bem-vindos. Abra uma [issue](https://github.com/dioguit0s/Convive-WebApp/issues/new) descrevendo o problema ou a proposta antes de submeter um PR grande.

## 👨‍💻 Autores

**Diogo Santos Rodrigues**
*Engenheiro / Arquiteto de Software*
[GitHub](https://github.com/dioguit0s)

**Leonardo Rosário Teixeira**
*Engenheiro de Software*
[GitHub](https://github.com/leonardorosario)

## 📄 Licença

Este projeto está sob a licença [MIT](LICENSE). Sinta-se à vontade para utilizá-lo, modificá-lo e hospedá-lo.
