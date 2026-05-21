# 🏢 Convive - Plataforma de Gestão Condominial

O **Convive** é um sistema web robusto projetado para modernizar, centralizar e facilitar a convivência e a administração de condomínios. Desenvolvido com foco na eficiência operacional e na clareza de comunicação, a plataforma conecta moradores e a administração (síndicos/moderadores) de forma ágil e segura.

## ✨ Funcionalidades Principais

A aplicação é dividida em dois domínios principais de acesso, garantindo a separação de responsabilidades e segurança através do Spring Security:

### 👤 Portal do Morador

* **Home/Mural**: Visualização de comunicados importantes emitidos pela administração.
* **Gestão de Reservas**: Interface para solicitação, visualização e cancelamento de reservas de áreas comuns (ex: salão de festas, churrasqueira).
* **Ocorrências**: Abertura e acompanhamento do status de ocorrências, reclamações ou sugestões.
* **Notificações**: Central de alertas em tempo real sobre mudanças de status nas solicitações.

### 🛡️ Dashboard Operacional (Moderador)

* **Visão Geral**: Dashboard com métricas e gráficos de uso do condomínio.
* **Triagem de Reservas**: Aprovação, rejeição e gerenciamento de conflitos de agenda nas áreas comuns.
* **Triagem de Ocorrências**: Análise, mudança de status e resolução de problemas relatados pelos moradores.
* **Gestão de Moradores**: Controle de usuários, envio de advertências e gerenciamento de perfis.
* **Comunicação**: Criação e disparo de comunicados globais.

### ⚙️ Recursos de Sistema

* **Event-Driven Notifications**: Uso de *Application Events* do Spring (`OcorrenciaCriadaEvent`, `ReservaRejeitadaEvent`, etc.) para desacoplar a lógica de negócio do envio de e-mails.
* **Recuperação de Senha**: Fluxo completo de *Forgot Password* com geração de tokens seguros e envio de links por e-mail.
* **CI/CD Integrado**: Pipelines configuradas via GitHub Actions para integração e entrega contínuas.

---

## 🛠️ Tecnologias e Arquitetura

O projeto foi construído seguindo o padrão arquitetural **MVC (Model-View-Controller)**, priorizando código limpo e fácil manutenção, características essenciais de uma boa engenharia de software.

**Back-end:**

* **Java 17+**
* **Spring Boot** (Web, Data JPA, Security, Mail)
* **Maven** (Gerenciamento de dependências)

**Front-end:**

* **Thymeleaf** (Motor de templates para renderização Server-Side)
* **HTML5, CSS3 e JavaScript (Vanilla)**
* **TailwindCSS** (Estilização utilitária e design responsivo)

**Infraestrutura & DevOps:**

* **Banco de Dados Relacional** (Mapeamento via Hibernate/JPA)
* **GitHub Actions** (Pipelines de CI/Deploy)

---

## 📂 Estrutura do Projeto

O back-end está organizado de forma modular no pacote `src/main/java/com/EC6/Convive/`:

* `Config/`: Configurações globais de segurança (SecurityConfig), MVC e inicialização de dados.
* `Controller/`: Controladores responsáveis por expor as rotas e gerenciar o fluxo das views (`Moderador`, `Morador`, `Public`).
* `Model/`: Entidades de domínio mapeadas para o banco de dados (ex: `Usuario`, `Reserva`, `Ocorrencia`).
* `Repository/`: Interfaces Spring Data JPA para acesso a dados.
* `Service/`: Camada de regras de negócio isoladas das rotas web.
* `Event/ & Listener/`: Implementação de padrão Observer para disparos de e-mail e notificações assíncronas.
* `Security/`: Implementações customizadas de `UserDetailsService` para autenticação.

---

## 👨‍💻 Autores

**Diogo Santos Rodrigues**
*Engenheiro / Arquiteto de Software*
[GitHub](https://github.com/dioguit0s)

**Leonardo Rosário Teixeira**
*Engenheiro de Software*
[GitHub](https://github.com/leonardorosario)

---

## 📄 Licença

Este projeto está sob a licença [MIT](https://opensource.org/licenses/MIT). Sinta-se à vontade para utilizá-lo e modificá-lo.
