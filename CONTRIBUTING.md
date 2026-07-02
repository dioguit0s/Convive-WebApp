# Contribuindo com o Convive

Obrigado pelo interesse em contribuir! O Convive é mantido como projeto open source e self-hosted — issues e PRs são bem-vindos.

## Rodando o projeto localmente

Veja a seção "Quickstart" no [README](README.md) para subir a aplicação via `docker compose up` ou via `./mvnw spring-boot:run` (perfil de desenvolvimento com H2 embutido, sem precisar de Docker).

## Rodando os testes

```bash
cd Convive
./mvnw clean test
```

A suíte roda automaticamente em cada Pull Request via GitHub Actions (`.github/workflows/ci.yml`).

## Abrindo uma issue

Antes de abrir um PR grande, abra uma [issue](https://github.com/dioguit0s/Convive-WebApp/issues/new) descrevendo o problema ou a proposta. Isso evita retrabalho caso a abordagem precise de ajuste antes de virar código.

## Enviando um Pull Request

* Crie um branch a partir da branch principal do repositório, com um nome descritivo (ex: `fix/nome-do-bug`, `feat/nome-da-feature`).
* Mantenha o PR focado em uma mudança por vez — PRs pequenos são revisados mais rápido.
* Descreva o que mudou e por quê; se corrigir um bug, descreva como reproduzi-lo antes da correção.
* Certifique-se de que `./mvnw clean test` passa localmente antes de abrir o PR.

## Código de conduta

Seja respeitoso nas discussões de issues e PRs. Críticas técnicas são bem-vindas; ataques pessoais não.
