# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Convive is a self-hosted condominium management platform (Portuguese/Brazilian domain). Server-rendered MVC monolith — Spring Boot + Thymeleaf, no SPA/frontend framework. The `package.json` at the repo root only declares `impeccable` as a dev dependency; there is no JS build — all actual application code lives under `Convive/`.

## Commands

All commands run from the `Convive/` subdirectory (the Maven project root), not the repo root.

```bash
cd Convive
./mvnw clean test              # run the full test suite
./mvnw spring-boot:run          # run locally with the dev profile (embedded H2, no Docker needed)
./mvnw clean package -DskipTests  # build the deployable jar (used by CI/CD, see deploy.yml)
```

Run a single test class or method:
```bash
./mvnw test -Dtest=OcorrenciaServiceTest
./mvnw test -Dtest=OcorrenciaServiceTest#someMethodName
```

Self-hosted/production-like run via Docker (Postgres instead of H2):
```bash
cp .env.example .env   # set APP_REMEMBER_ME_KEY and other vars first
docker compose up --build   # app on http://localhost:8085
```

There is no lint/format command configured in this repo.

## Architecture

### Package layout (`Convive/src/main/java/com/EC6/Convive/`)

- `Config/` — `SecurityConfig` (Spring Security rules), `DataInitializer` (seeds demo data on first boot when tables are empty — moderator + moradores + reservas + ocorrências + comunicados), `MvcConfig`, `AsyncConfig` (defines the `MAIL_TASK_EXECUTOR` used for async email sending), `AuthenticatedUserModelAdvice` (`@ControllerAdvice` that injects the current authenticated `Usuario`, freshly reloaded from the DB, into every view's model as `usuario`).
- `Controller/` — one controller per feature area, split by audience: `Moderador*`/`Triagem*` controllers (moderator/admin dashboard flows) vs `Morador*` controllers (resident-facing flows) vs public controllers (`IndexController`, `LoginController`, `ContactController`).
- `Model/` — JPA entities. `Usuario` is an abstract `@Entity` with `InheritanceType.JOINED`; `Moderador` and `Morador` extend it and add `getTipoUsuario()`. Most feature entities (`Reserva`, `Ocorrencia`, `Notificacao`, `Comunicado`, `AreaComum`) reference a `Usuario`/`Moderador`/`Morador`.
- `Repository/` — Spring Data JPA interfaces, one per entity.
- `Service/` — business logic, kept out of controllers. Controllers call services; services call repositories and publish domain events.
- `Event/` + `Listener/` — Observer pattern for decoupling side effects (mainly email notifications) from the request flow. Services publish events (e.g. `OcorrenciaCriadaEvent`, `ReservaPendenteCriadaEvent`, `ReservaRejeitadaEvent`); `NotificationEmailListener` handles them with `@Async(AsyncConfig.MAIL_TASK_EXECUTOR)` + `@EventListener`, and swallows/logs per-recipient failures so one bad email doesn't break the others.
- `Security/` — `CustomUserDetailsService` + `CustomUserDetails`, backing Spring Security auth against the `Usuario` hierarchy (a user's role comes from whether it's a `Moderador` or `Morador` row).
- `Exception/` — single `GlobalExceptionHandler` (`@ControllerAdvice`) catching all `Exception`s, logging an error code, and rendering `public/error`.
- `Util/`, `Validator/`, `dto/` — helpers, form validators, and view/chart DTOs (e.g. `DashboardDataDto`, `ChartSliceDto` for the moderator dashboard).

### Authorization model

Defined centrally in `SecurityConfig`: `/morador/**` requires `ROLE_MORADOR` or `ROLE_MODERADOR`; `/moderador/**` requires `ROLE_MODERADOR` only; a fixed set of public paths (`/`, `/login`, `/features`, `/about`, `/contact`, `/privacy`, `/terms`, `/forgot-password`, `/reset-password`, static assets) is open; everything else requires authentication. The H2 console and its CSRF/frame exemptions are only wired up when `spring.h2.console.enabled=true` (dev profile), never in prod.

### Views

Thymeleaf templates under `src/main/resources/templates/`, split into `moderador/`, `morador/`, `public/`, `email/`, plus shared `fragments/`. Styling is TailwindCSS + plain CSS/JS under `src/main/resources/static/`.

### Config profiles

- `application.properties` — default/dev profile: embedded H2 file DB, H2 console enabled, `spring.thymeleaf.cache=false`. Optionally imports `application-local.properties` (gitignored) for machine-specific overrides.
- `application-prod.properties` — Postgres via env vars, H2 console disabled. Activated by `SPRING_PROFILES_ACTIVE=prod`, which `docker-compose.yml` sets automatically.
- Secrets (`APP_REMEMBER_ME_KEY`, SMTP creds, `POSTGRES_PASSWORD`) are supplied via `.env` (see `.env.example`), never committed.

### CI/CD

- `.github/workflows/ci.yml` — on every PR to `main`: JDK 23 Temurin, runs `./mvnw clean test` from `Convive/`.
- `.github/workflows/deploy.yml` — on push to `main`: builds the jar, deploys it to a self-hosted runner, restarts `convive.service`, and checks the systemd service came up healthy.

### Known standing risk

The app always seeds a demo moderator account (`moderador@convive.com` / `moderador123`) via `DataInitializer` on first boot, including in prod — this is documented behavior (see README), not a bug to silently "fix" by removing the seeding logic.
