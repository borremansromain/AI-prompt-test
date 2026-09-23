# Dev guide — stock-management API

Stack cible : Java 21 / Spring Boot 3.3 (Clean Architecture) + Keycloak 26 (OAuth2 RS) + PostgreSQL 16.
Methodologie : Spec → Implémentation → Vérification → Feedback → Correction (voir `prompt.md`).

## Démarrage rapide — 2 chemins

### A) Docker Compose (recommandé, garanti — tout dans des conteneurs)

```bash
docker compose up
```

- App : http://localhost:8080  (santé : `http://localhost:8080/actuator/health`)
- Postgres : `localhost:5432` — `stock_db` / `stock_user` / `secret_dev_only`
- Keycloak UI : http://localhost:8081 — admin `admin` / `admin`
- Realm : `stock-app` (les 3 rôles + 3 utilisateurs arrivent au **Prompt 2.1**)

L'`app` atteint `postgres:5432` et `keycloak:8080` via les hostname du réseau compose.

### B) Dev Container (VS Code + Java 21 + Maven)

`Reopen in Container`, puis sur l'hôte :

```bash
docker compose up -d postgres keycloak   # postgres + keycloak de l'API
```

Et dans le terminal du dev container :

```bash
mvn spring-boot:run                       # app locale, reach DB/KC via host.docker.internal
```

Le dev container pointe `DB_HOST=host.docker.internal` et `KEYCLOAK_ISSUER=http://host.docker.internal:8081/...`.

## Variables d'environnement

Voir `.env.example` (copier vers `.env`). Les valeurs par défaut utilisent `localhost` (chemin B / hôte).
Le compose passe en surcharge : `DB_HOST=postgres` et `KEYCLOAK_ISSUER=http://keycloak:8080/...`.

## Build & vérification

```bash
mvn clean compile          # compile (critère de fini du Prompt 1.3)
mvn checkstyle:check       # style
mvn test                   # tests (Phase 3+)
mvn verify                 # compile + checkstyle + spotbugs + jacoco
```

## Rôles & accès (matrice — appliquée au Prompt 5.1)

| Endpoint                    | GET     | POST         | PUT          | DELETE   |
|-----------------------------|---------|--------------|--------------|----------|
| `/api/v1/categories`        | VIEWER+ | MANAGER+     | MANAGER+     | ADMIN    |
| `/api/v1/products`          | VIEWER+ | MANAGER+     | MANAGER+     | ADMIN    |
| `/api/v1/products/low-stock`| VIEWER+ | —            | —            | —        |
| `/api/v1/stock-movements`   | VIEWER+ | MANAGER+     | —            | ADMIN    |

`/actuator/health`, `/actuator/info`, `/v3/api-docs`, `/swagger-ui` = `permitAll`.
