Démonstration technique — Back-end de gestion de stock
Java 21 Spring Boot (Clean Architecture) + Keycloak + PostgreSQL + Dev Container + MCP Server
But du document. Enchaîner, dans l'ordre, tous les prompts nécessaires pour construire une API REST de gestion de stock en Java 21 / Spring Boot 3.x, architecturée en Clean Architecture (domaine pur Java, Spring comme adapter), sécurisée par Keycloak (OAuth2 Resource Server), avec PostgreSQL 16, Dev Container, Flyway, et exposée via un serveur MCP pour faciliter le développement du front-end. Même méthodologie que les guides PHP et Angular : cycle Spec → Implémentation → Vérification → Feedback → Correction, chaque prompt accompagné d'un critère de « fini » vérifiable.

Copie chaque prompt tel quel dans une session Claude (idéalement Claude Code, dans le repo, pour la boucle la plus courte).

0. Versions et dépendances cibles (à figer avant de commencer)
   Composant Version Note
   Java 21 (LTS) Records, sealed classes, pattern matching, virtual threads, text blocks
   Framework Spring Boot 3.3.x Web, Security, Data JPA, Validation
   Build Maven 3.9+ (ou Gradle 8.x si préférence)
   Serveur web Tomcat embarqué (Spring Boot default) Alternative : Jetty, Undertow
   Base de données PostgreSQL 16 Via Spring Data JPA (Hibernate 6)
   Migrations Flyway 10 Schéma versionné, standard Spring Boot
   Auth Spring Security OAuth2 Resource Server Validation JWT locale via JWKS (RS256)
   Validation Jakarta Bean Validation (hibernate-validator) @NotNull, @Size, etc.
   Tests JUnit 5 + Mockito + Spring Boot Test Unitaire + intégration (@DataJpaTest, @WebMvcTest, @SpringBootTest)
   Qualité Checkstyle + SpotBugs + JaCoCo Analyse statique, bugs, couverture
   Formatage Spotless (google-java-format ou palantir-java-format) Style cohérent
   Keycloak 26.x Distribution Quarkus (authentification & rôles)
   Environnement de dev Dev Container (spec devcontainer.json) + Docker Compose Backend Java + PostgreSQL + Keycloak orchestrés
   Serveur MCP Stdio-based (Node.js ou Python) Expose l'API pour les outils IA/front-end dev
   API Docs springdoc-openapi-starter-webmvc-ui (Swagger UI) Auto-généré depuis les annotations
   Architecture globale — Clean Architecture
   ┌─────────────────────────────────────────────────────────────────────────┐
   │ SPRING FRAMEWORK LAYER │
   │ ┌──────────────────────────────────────────────────────────────────┐ │
   │ │ Web (Spring MVC) Security (OAuth2) Data (JPA/Hibernate)│ │
   │ │ ├─ @RestController ├─ SecurityFilterChain├─ @Repository │ │
   │ │ ├─ @ControllerAdvice ├─ JWT Validation ├─ EntityManager │ │
   │ │ └─ ExceptionHandler └─ RoleBasedAccess └─ Spring Data │ │
   │ └──────────────────────────────────────────────────────────────────┘ │
   │ ▲ │
   │ ADAPTERS │ (Drivers/Implementations) │
   └─────────────────────────────────────┼─────────────────────────────────┘
   │
   ┌─────────────────────────────┼─────────────────────────────────┐
   │ PORTS (Interfaces — Core) │
   │ ├─ CategoryRepositoryPort ├─ AuthGatewayPort │
   │ ├─ ProductRepositoryPort ├─ EventPublisherPort │
   │ ├─ StockMovementRepoPort ├─ ClockPort │
   │ └─ LoggerPort (rare, car SLF4J est déjà abstrait) │
   └─────────────────────────────┬─────────────────────────────────┘
   │
   ┌─────────────────────────────┼─────────────────────────────────┐
   │ APPLICATION LAYER (Orchestration) │
   │ ┌─────────────────────────────────────────────────────────┐ │
   │ │ USE CASES (Services / Interactors) │ │
   │ │ ├─ CreateCategoryUseCase │ │
   │ │ ├─ CreateProductUseCase │ │
   │ │ ├─ RecordStockMovementUseCase │ │
   │ │ └─ QueryProductsByAlertThresholdUseCase │ │
   │ └─────────────────────────────────────────────────────────┘ │
   │ ┌─────────────────────────────────────────────────────────┐ │
   │ │ DTOs (Records — Input/Output) │ │
   │ │ ├─ CreateCategoryCommand / CategoryDto │ │
   │ │ ├─ CreateProductCommand / ProductDto │ │
   │ │ └─ RecordStockMovementCommand / StockMovementDto │ │
   │ └─────────────────────────────────────────────────────────┘ │
   └─────────────────────────────┬─────────────────────────────────┘
   │
   ┌─────────────────────────────┼─────────────────────────────────┐
   │ CORE DOMAIN LAYER (Business Logic — PUR Java) │
   │ ┌─────────────────────────────────────────────────────────┐ │
   │ │ ENTITIES (Javabean ou Record, SANS annotation JPA) │ │
   │ │ ├─ Category (id, name, description) │ │
   │ │ ├─ Product (sku, price, qty, alertThreshold) │ │
   │ │ └─ StockMovement (type, qty, reason, author) │ │
   │ └─────────────────────────────────────────────────────────┘ │
   │ ┌─────────────────────────────────────────────────────────┐ │
   │ │ VALUE OBJECTS & BUSINESS RULES (Records immuables) │ │
   │ │ ├─ Money, Quantity, Sku │ │
   │ │ ├─ StockMovementType (enum) │ │
   │ │ └─ Role (enum: ADMIN, MANAGER, VIEWER) │ │
   │ └─────────────────────────────────────────────────────────┘ │
   │ ┌─────────────────────────────────────────────────────────┐ │
   │ │ DOMAIN EVENTS (sealed interface) │ │
   │ │ ├─ ProductCreatedEvent │ │
   │ │ ├─ StockMovementRecordedEvent │ │
   │ │ └─ AlertThresholdExceededEvent │ │
   │ └─────────────────────────────────────────────────────────┘ │
   │ ┌─────────────────────────────────────────────────────────┐ │
   │ │ DOMAIN EXCEPTIONS (hierarchy) │ │
   │ │ ├─ DomainException (base) │ │
   │ │ ├─ InsufficientStockException │ │
   │ │ ├─ DuplicateSkuException │ │
   │ │ └─ EntityNotFoundException │ │
   │ └─────────────────────────────────────────────────────────┘ │
   └─────────────────────────────────────────────────────────────┘

FLUX: HTTP → Controller (Adapter) → UseCase (App) → Entity (Core) ← RepositoryPort (Core)
↑ implémenté par Spring Data JPA (Adapter/Infra)
Règle d'or : Le domaine (Core) est pur Java — zéro annotation Spring, zéro annotation JPA. Spring est un détail d'implémentation qui vit dans les couches externes. Les ports (interfaces) sont dans le Core ; leurs implémentations Spring Data vivent dans l'Infrastructure/Adapter.

PHASE 1 — Fondations et environnement
Prompt 1.1 — Cadrage et validation des hypothèses
Je veux construire une API REST de gestion de stock en Java 21 / Spring Boot 3.3 avec CLEAN ARCHITECTURE.

Architecture : CLEAN ARCHITECTURE (Robert C. Martin) appliquée à Spring Boot.

- Core (Domain) au centre : entities (pur Java, SANS @Entity JPA), value objects (records),
  domain events (sealed interface), domain exceptions, PORTS (interfaces).
- Application layer : use cases (services), DTOs (records), orchestration.
  Peut utiliser @Service mais JAMAIS @Entity, @Repository, @Table.
- Adapter/Infrastructure layer :
  - Spring MVC controllers (@RestController)
  - Spring Security (OAuth2 Resource Server, JWT via JWKS)
  - Spring Data JPA repositories (implémentent les ports du domaine)
  - JPA entities (@Entity) qui MAPPENT les entities du domaine (mapper séparé)
  - Configuration classes, Flyway, Bean factories
- Dépendances centripètes : le domaine ne dépend de RIEN.
  L'application dépend du domaine. L'infrastructure dépend de l'application ET du domaine.

Stack imposée :

- Java 21 (records, sealed classes, pattern matching, virtual threads)
- Spring Boot 3.3.x (Web, Security, Data JPA, Validation, Actuator)
- PostgreSQL 16 via Spring Data JPA (Hibernate 6)
- Flyway pour les migrations
- Keycloak 26 : validation JWT locale via JWKS (spring-security-oauth2-resource-server)
- 3 rôles : STOCK_ADMIN, STOCK_MANAGER, STOCK_VIEWER (realm roles)
- Dev Container + Docker Compose (JVM, PostgreSQL, Keycloak)
- OpenAPI 3.0 (springdoc) + serveur MCP (Node.js ou Python)
- Build : Maven

Avant tout code :

1. Reformule et liste tes hypothèses.
2. Explique les 4 couches et leurs dépendances, en précisant ce qui est "pur Java"
   et ce qui est "Spring".
3. Propose l'arborescence Maven (packages) complète :
   com.stock.core.domain, com.stock.core.domain.port, com.stock.application.usecase,
   com.stock.application.dto, com.stock.adapter.inbound.web, com.stock.adapter.outbound.persistence,
   com.stock.adapter.outbound.security, com.stock.infrastructure.config
4. Tableau d'endpoints complet avec matrice rôles/permissions.
5. Ordre des tranches verticales.
6. Dépendances Maven justifiées (poms).
7. Stratégie de mapping : domaine ↔ JPA entities (mapper manuel vs MapStruct).
8. Description du serveur MCP.

Ne code rien. Validation d'abord.
Critère de « fini » : Arborescence packages complète, tableau endpoints/rôles, dépendances Maven justifiées, stratégie de mapping expliquée, règle "domaine pur" explicite.

Prompt 1.2 — Dev Container et Docker Compose (reproductibilité)
Priorité 1. Avant le build, on établit l'environnement reproductible. Le Dev Container est le socle : tout le reste tourne dedans. git clone → Reopen in Container → c'est lancé.

Génère l'infrastructure Dev Container (BACKEND ONLY) :

1. docker-compose.yml avec 3 services :
   - app :
     image: eclipse-temurin:21-jdk-alpine (ou maven:3.9-eclipse-temurin-21)
     workdir: /workspace
     command: mvn spring-boot:run (ou java -jar pour prod)
     depends_on: postgres (healthy), keycloak (healthy)
     environment: SPRING_PROFILES_ACTIVE=dev, DB_HOST=postgres, DB_PORT=5432,
     KEYCLOAK_ISSUER=http://keycloak:8080/realms/stock-app
     ports: 8080:8080
   - postgres :
     image: postgres:16-alpine
     environment: POSTGRES_DB=stock_db, POSTGRES_USER=stock_user, POSTGRES_PASSWORD=secret_dev_only
     volumes: postgres_data:/var/lib/postgresql/data
     healthcheck: pg_isready
     ports: 5432:5432
   - keycloak :
     image: quay.io/keycloak/keycloak:26
     command: ["start-dev", "--import-realm"]
     environment: KC_BOOTSTRAP_ADMIN_PASSWORD=admin, KC_HEALTH_ENABLED=true
     volumes: ./config/keycloak-realm.json:/opt/keycloak/data/import/realm.json
     healthcheck: curl -f http://localhost:9000/health/ready
     ports: 8081:8080 (Keycloak UI)

   volumes:
   postgres_data:

2. .devcontainer/devcontainer.json :
   - image: mcr.microsoft.com/vscode/code:latest (ou eclipse-temurin:21-jdk)
   - Mounts : code en /workspace
   - postCreateCommand : mvn dependency:go-offline && mvn checkstyle:check spotbugs:check
   - Ports forwarded : 8080 (app), 5432 (postgres), 8081 (keycloak UI)
   - Extensions VS Code : Extension Pack for Java, SQLTools, REST Client, Docker
   - features : ghcr.io/devcontainers/features/docker-in-docker (si compose from inside)

3. .devcontainer/post-create.sh :
   - docker compose up -d postgres keycloak
   - Attend healthy
   - Affiche URLs, identifiants de test, endpoints

4. .env.example :
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=stock_db
   DB_USER=stock_user
   DB_PASSWORD=secret_dev_only
   KEYCLOAK_ISSUER=http://localhost:8081/realms/stock-app
   JWT_JWKS_URI=http://localhost:8081/realms/stock-app/protocol/openid-connect/certs
   LOG_LEVEL=DEBUG
   SPRING_PROFILES_ACTIVE=dev

5. .gitignore : target/, .env, .idea/, \*.iml, mcp/node_modules

Explique la liaison services via hostname (app→postgres:5432, app→keycloak:8080).
Explique pourquoi on use docker-compose DEDANS le dev container (ou via docker socket).
Critère de « fini » : git clone → Reopen in Container → docker compose up → tous services healthy, l'app Spring répond sur localhost:8080/actuator/health, PostgreSQL sur localhost:5432, Keycloak sur localhost:8081.

Prompt 1.3 — Squelette Maven et bootstrap Spring Boot
Génère les fondations Java/Spring :

1. pom.xml (Maven) :
   - parent : spring-boot-starter-parent 3.3.x
   - java.version : 21
   - Dépendances :
     - spring-boot-starter-web
     - spring-boot-starter-security
     - spring-boot-starter-oauth2-resource-server
     - spring-boot-starter-data-jpa
     - spring-boot-starter-validation
     - spring-boot-starter-actuator
     - springdoc-openapi-starter-webmvc-ui (Swagger UI)
     - flyway-core + flyway-database-postgresql
     - postgresql (runtime)
     - lombok (OPTIONNEL — si non, on utilise records + builders manuels)
     - MAPSTRUCT (mapstruct + mapstruct-processor) pour mapping domaine↔JPA
   - Dépendances test :
     - spring-boot-starter-test
     - spring-security-test
     - testcontainers (postgres) pour intégration
   - Plugins :
     - maven-compiler-plugin (release 21)
     - maven-surefire-plugin
     - checkstyle-maven-plugin
     - spotbugs-maven-plugin
     - jacoco-maven-plugin
     - spring-boot-maven-plugin
     - git-commit-id-plugin (optionnel, pour /debug/info)

2. Arborescence packages (src/main/java/com/stock/) :
   - StockApplication.java (@SpringBootApplication)
   - core/domain/entity/
   - core/domain/valueobject/
   - core/domain/event/
   - core/domain/exception/
   - core/domain/port/
   - application/usecase/
   - application/dto/
   - adapter/inbound/web/controller/
   - adapter/inbound/web/handler/ (GlobalExceptionHandler)
   - adapter/inbound/web/config/ (SecurityConfig, OpenApiConfig)
   - adapter/outbound/persistence/ (JPA entities, Spring Data repos, mappers)
   - adapter/outbound/security/ (TokenClaimsAdapter, RoleMapper)
   - infrastructure/config/ (AppProperties, BeanFactory)
   - infrastructure/health/ (CustomHealthIndicators)

3. application.yml :
   ```yaml
   spring:
     application:
       name: stock-api
     datasource:
       url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:stock_db}
       username: ${DB_USER:stock_user}
       password: ${DB_PASSWORD:}
       hikari:
         maximum-pool-size: 10
     jpa:
       hibernate:
         ddl-auto: validate
       open-in-view: false
       properties:
         hibernate:
           format_sql: true
     flyway:
       enabled: true
       locations: classpath:db/migration
     security:
       oauth2:
         resourceserver:
           jwt:
             issuer-uri: ${KEYCLOAK_ISSUER:http://localhost:8081/realms/stock-app}
   server:
     port: 8080
     error:
       include-stacktrace: never
       include-message: always
   logging:
     level:
       com.stock: ${LOG_LEVEL:DEBUG}
       org.springframework.security: INFO
   StockApplication.java : @SpringBootApplication + main()
   ```

application-dev.yml (profile dev) : debug logs, H2 en memory pour tests rapides (optionnel)

.editorconfig : style Java (indentation 4, UTF-8, LF)

Explique le choix Maven vs Gradle, et pourquoi on active Flyway + JPA validate (pas create).

**Critère de « fini » :** `mvn clean compile` passe, `mvn spring-boot:run` démarre, `curl localhost:8080/actuator/health` → `{"status":"UP"}`, `mvn checkstyle:check` sans erreur.

---

## Prompt 1.4 — Migrations PostgreSQL (Flyway)

Génère les migrations de schéma via Flyway :

src/main/resources/db/migration/ :

V001\_\_init_schema.sql
Schéma (DDL) :

CREATE TABLE categories ( id UUID PRIMARY KEY DEFAULT gen_random_uuid(), name VARCHAR(255) NOT NULL UNIQUE, description TEXT, created_at TIMESTAMPTZ NOT NULL DEFAULT now() );
CREATE TABLE products ( id UUID PRIMARY KEY DEFAULT gen_random_uuid(), sku VARCHAR(50) NOT NULL UNIQUE, name VARCHAR(255) NOT NULL, category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE, price NUMERIC(10,2) NOT NULL CHECK (price > 0), quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0), alert_threshold INT NOT NULL DEFAULT 0 CHECK (alert_threshold >= 0), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now() );
CREATE TABLE stock_movements ( id UUID PRIMARY KEY DEFAULT gen_random_uuid(), product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE, type VARCHAR(10) NOT NULL CHECK (type IN ('ENTRY', 'EXIT')), quantity INT NOT NULL CHECK (quantity > 0), reason TEXT, author_username VARCHAR(255) NOT NULL, movement_date TIMESTAMPTZ NOT NULL DEFAULT now(), created_at TIMESTAMPTZ NOT NULL DEFAULT now() );
Indexes :

CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_stock_movements_product_date ON stock_movements(product_id, movement_date DESC);
Optionnel : src/main/resources/db/migration/R001\_\_seed_data.sql (repeatable)
ou un script séparé seed.sql à exécuter manuellement.

Vérifie que Flyway + JPA (ddl-auto: validate) sont cohérents :
les noms de colonnes JPA doivent correspondre au schéma Flyway.

Justifie Flyway vs Liquibase, et pourquoi ddl-auto=validate (Flyway est la source de vérité).

**Critère de « fini » :** `mvn spring-boot:run` exécute Flyway au démarrage, tables créées dans PostgreSQL. `SELECT * FROM categories;` fonctionne. `ddl-auto: validate` ne lève pas d'erreur.

---

# PHASE 2 — Keycloak

## Prompt 2.1 — Realm, rôles, utilisateurs de test

Configure Keycloak 26 :

Realm « stock-app » (export JSON dans config/keycloak-realm.json) :

3 realm roles : STOCK_ADMIN, STOCK_MANAGER, STOCK_VIEWER
3 utilisateurs :
alice@stock.local / pwd: alice123 / rôle: STOCK_ADMIN
bob@stock.local / pwd: bob123 / rôle: STOCK_MANAGER
carol@stock.local / pwd: carol123 / rôle: STOCK_VIEWER
Client OIDC « stock-api » (Bearer-only / confidential) :

Client ID: stock-api
Client Secret: (généré, noté en .env.example)
Access Type: confidential
Valid Redirect URIs: http://localhost:8080/callback (pour le test)
Web Origins: http://localhost:3000 (front-end)
Claims mappés :
realm_access.roles → claim "roles" (array)
preferred_username
email
Access Type: bearer-only (pas de code flow pour l'API)
Import automatique : le realm JSON est monté dans le conteneur Keycloak
et importé via --import-realm au démarrage (command: ["start-dev", "--import-realm"]).

JWKS URI (pour le resource server Spring) :
http://keycloak:8080/realms/stock-app/protocol/openid-connect/certs
En dev local : http://localhost:8081/realms/stock-app/protocol/openid-connect/certs

Génération de token de test (pour curl) :
curl -X POST http://localhost:8081/realms/stock-app/protocol/openid-connect/token

-d grant_type=password

-d client_id=stock-api

-d client_secret=SECRET

-d username=alice@stock.local

-d password=alice123

OU via Keycloak Admin UI (Access Token tab).

Décris comment le realm JSON est importé au démarrage du Dev Container.
Explique le flow Bearer-only vs Authorization Code.

**Critère de « fini » :** Realm importé, 3 utilisateurs accessibles dans Keycloak Admin, client « stock-api » existe, un token est obtenable via endpoint token.

---

# PHASE 3 — Core Domain (pur Java, indépendant de Spring)

> **Règle absolue :** Ce package ne contient AUCUNE annotation Spring, AUCUNE annotation JPA,
> AUCUNE dépendance à spring-\*, jpa, hibernate. Seulement du Java pur + Jakarta Validation
> (optionnel, ou validation manuelle dans les constructors).

## Prompt 3.1 — Entities et Value Objects

Construis le cœur métier (Core Domain) — pur Java 21, testable sans Spring.

Convention :

Entities = classes Java classiques (pas records, car mutables via méthodes)
Value Objects = records Java (immuables)
Domain Events = sealed interface + records implémentant
Exceptions = hiérarchie sémantique
com.stock.core.domain.valueobject.\* :

public record Money(BigDecimal amount, String currency) {
public Money {
if (amount == null || amount.signum() <= 0)
throw new IllegalArgumentException("Money amount must be > 0");
if (currency == null || currency.isBlank())
throw new IllegalArgumentException("Currency is required");
}
// default currency = "EUR" via compact constructor ou factory
}

public record Quantity(int value) {
public Quantity {
if (value < 0) throw new IllegalArgumentException("Quantity must be >= 0");
}
}

public record Sku(String value) {
public Sku {
if (value == null || value.length() < 3 || value.length() > 50)
throw new IllegalArgumentException("SKU must be 3-50 chars");
}
}

com.stock.core.domain.entity.Category :
public class Category {
private final UUID id;
private String name;
private String description;
private final Instant createdAt;

// Constructor (id peut être null pour création, généré par le repository)
public Category(UUID id, String name, String description, Instant createdAt) {
// validations
}

// Factory : Category.create(name, description) → id = null
// with\* methods pour modifications (retournent cette instance, mutable contrôlée)
}

com.stock.core.domain.entity.Product :
public class Product {
private final UUID id;
private Sku sku;
private String name;
private UUID categoryId;
private Money price;
private Quantity quantity;
private int alertThreshold;
private Instant createdAt;
private Instant updatedAt;

public Product { ... validations ... }

// MÉTHODE MÉTIER :
public void applyStockMovement(StockMovementType type, Quantity delta) {
int newQty = switch (type) {
case ENTRY -> this.quantity.value() + delta.value();
case EXIT -> {
if (this.quantity.value() - delta.value() < 0)
throw new InsufficientStockException(sku, delta);
yield this.quantity.value() - delta.value();
}
};
this.quantity = new Quantity(newQty);
this.updatedAt = Instant.now();

if (this.quantity.value() <= this.alertThreshold && this.alertThreshold > 0) {
// émet AlertThresholdExceededEvent (via event collector ou direct)
}
}
}

com.stock.core.domain.entity.StockMovement :
public record StockMovement(
UUID id,
UUID productId,
StockMovementType type,
Quantity quantity,
String reason,
String authorUsername,
Instant movementDate,
Instant createdAt
) { ... }

com.stock.core.domain.event.\* :
public sealed interface DomainEvent permits ProductCreatedEvent,
StockMovementRecordedEvent, AlertThresholdExceededEvent {
Instant occurredAt();
UUID aggregateId();
}

public record ProductCreatedEvent(UUID productId, Sku sku, Instant occurredAt)
implements DomainEvent {
public UUID aggregateId() { return productId; }
}
// ... idem pour les autres

com.stock.core.domain.exception.\* :
public abstract class DomainException extends RuntimeException { ... }
public class InsufficientStockException extends DomainException { ... }
public class DuplicateSkuException extends DomainException { ... }
public class EntityNotFoundException extends DomainException { ... }
public class ValidationException extends DomainException { ... }

Tests unitaires (JUnit 5, SANS Spring) :

Quantity(-5) → IllegalArgumentException
Sku("AB") → IllegalArgumentException
Money(BigDecimal.ZERO) → IllegalArgumentException
Product.applyStockMovement(EXIT, 200) sur qty=100 → InsufficientStockException
Product.applyStockMovement(ENTRY, 50) sur qty=100 → qty=150
ProductCreatedEvent : occurredAt non-null, aggregateId correct
Explique pourquoi le domaine pur (zéro framework) garantit testabilité et réutilisabilité.
Explique le choix mutable (entity) vs immutable (VO).

**Critère de « fini » :** `mvn test` passe sur les tests du domaine. Aucune import spring/jpa dans `core.domain`. Les entities valident leurs invariants. Les value objects sont immuables.

---

## Prompt 3.2 — Ports (interfaces du domaine)

Définis les ports — contrats entre domaine et adapters.

Ces interfaces vivent dans le CORE DOMAIN. Elles définissent ce dont le domaine a besoin.
Leurs implémentations (Spring Data, JPA) vivent dans l'Adapter/Infrastructure.

com.stock.core.domain.port.CategoryRepositoryPort :
public interface CategoryRepositoryPort {
List<Category> findAll();
Optional<Category> findById(UUID id);
Optional<Category> findByName(String name);
Category save(Category category);
void delete(UUID id);
boolean existsByName(String name);
}

com.stock.core.domain.port.ProductRepositoryPort :
public interface ProductRepositoryPort {
List<Product> findAll();
Optional<Product> findById(UUID id);
Optional<Product> findBySku(Sku sku);
Product save(Product product);
void delete(UUID id);
List<Product> findByAlertThresholdExceeded();
Page<Product> findAll(Pageable pageable);
}

com.stock.core.domain.port.StockMovementRepositoryPort :
public interface StockMovementRepositoryPort {
void save(StockMovement movement);
List<StockMovement> findByProductId(UUID productId);
Page<StockMovement> findAll(Pageable pageable);
}

com.stock.core.domain.port.AuthGatewayPort :
public interface AuthGatewayPort {
TokenClaims authenticate(String jwtToken);
}
// TokenClaims = record(UUID userId, String username, String email, Set<Role> roles)

com.stock.core.domain.port.EventPublisherPort :
public interface EventPublisherPort {
void publish(DomainEvent event);
}

com.stock.core.domain.port.ClockPort :
public interface ClockPort {
Instant now();
}
// Rend le domaine testable (pas de new Date() dans le métier)

NOTE : LoggerPort n'est pas nécessaire car SLF4J est déjà une abstraction.
Le domaine peut utiliser Logger (org.slf4j.Logger) directement — c'est acceptable.

Règle : les ports ne connaissent QUE du domaine (entities, VOs, exceptions, events).
Aucune import de spring, jpa, hibernate, jdbc.

Tests : vérifie que les interfaces compilent, que les types sont cohérents.

**Critère de « fini » :** Les interfaces sont définies. `mvn compile` passe. `mvn checkstyle:check` passe. Aucune dépendance framework dans `core.domain.port`.

---

# PHASE 4 — Application Layer (Use Cases)

## Prompt 4.1 — Use Cases (Services d'application)

Construis les cas d'usage — orchestrateurs du domaine.

Convention :

Un UseCase = une classe qui implémente un use case spécifique
Injectée via CONSTRUCTEUR (ports du domaine + ClockPort)
Annotée @Service (Spring) — c'est la SEULE annotation Spring autorisée ici
Le use case orchestre : valide input → appelle domaine → persiste via port → émet events → rend DTO
com.stock.application.usecase.CreateCategoryUseCase :
@Service
public class CreateCategoryUseCase {
private final CategoryRepositoryPort repository;
private final ClockPort clock;

public CreateCategoryUseCase(CategoryRepositoryPort repository, ClockPort clock) {
this.repository = repository;
this.clock = clock;
}

public CategoryDto execute(CreateCategoryCommand command) {
// 1. Valide (command a déjà @Valid, mais double-check)
if (repository.existsByName(command.name()))
throw new ValidationException("Category name already exists");

// 2. Crée entity domaine
Category category = Category.create(command.name(), command.description(), clock.now());

// 3. Persiste
Category saved = repository.save(category);

// 4. Log
logger.info("Category created: {}", saved.getId());

// 5. Rend DTO
return new CategoryDto(saved.id(), saved.getName(), saved.getDescription(), saved.getCreatedAt());
}
}

CreateProductUseCase :

Injecté : ProductRepositoryPort, CategoryRepositoryPort, EventPublisherPort, ClockPort
execute(CreateProductCommand):
Vérifie catégorie existe (sinon EntityNotFoundException)
Vérifie SKU unique (sinon DuplicateSkuException)
Crée Product du domaine
product.applyStockMovement(ENTRY, command.quantity()) → initialise stock
repository.save(product)
eventPublisher.publish(new ProductCreatedEvent(...))
Rend ProductDto
RecordStockMovementUseCase :

Injecté : StockMovementRepositoryPort, ProductRepositoryPort, EventPublisherPort, ClockPort
execute(RecordStockMovementCommand):
Charge product via repository (EntityNotFoundException si absent)
product.applyStockMovement(command.type(), command.quantity()) → lève InsufficientStockException si EXIT > qty dispo
Crée StockMovement (avec author du token claims)
movementRepository.save(movement)
productRepository.save(product) (qty mise à jour)
eventPublisher.publish(new StockMovementRecordedEvent(...))
Si qty <= alertThreshold → eventPublisher.publish(new AlertThresholdExceededEvent(...))
Rend StockMovementDto
ListCategoriesUseCase, ListProductsUseCase (avec pagination),
GetProductUseCase, DeleteCategoryUseCase, etc.

Tests unitaires (JUnit 5 + Mockito, SANS Spring) :

Mock les ports (Mockito.mock(CategoryRepositoryPort.class))
Test : CreateCategory avec name existant → ValidationException
Test : CreateProduct avec catégorie inexistante → EntityNotFoundException
Test : RecordStockMovement EXIT > qty → InsufficientStockException
Test : ProductCreatedEvent est publié (verify(eventPublisher).publish(any()))
Test : AlertThresholdExceededEvent est publié si qty <= threshold
Explique pourquoi les UseCases sont "thin" : orchestration + mapping DTO.
La logique métier lourd (validation de stock, règles) reste dans les entities.

**Critère de « fini » :** `mvn test` passe sur tous les use cases. Un test verify que ProductCreatedEvent est émis. Un test verify que InsufficientStockException est levée.

---

## Prompt 4.2 — DTOs (Records — Commandes et Responses)

Définis les DTOs pour l'API.

Convention :

Commands (input) = records avec Jakarta Validation annotations
Dtos (output) = records purs (pas de validation)
Zéro logique métier dans les DTOs
com.stock.application.dto.command.\* :
public record CreateCategoryCommand(
@NotBlank @Size(min = 1, max = 255) String name,
@Size(max = 1000) String description
) {}

public record CreateProductCommand(
@NotBlank @Size(min = 3, max = 50) String sku,
@NotBlank @Size(max = 255) String name,
@NotNull UUID categoryId,
@NotNull @Positive BigDecimal price,
@NotNull @PositiveOrZero @Min(0) Integer quantity,
@NotNull @PositiveOrZero Integer alertThreshold
) {}

public record RecordStockMovementCommand(
@NotNull UUID productId,
@NotNull StockMovementType type,
@NotNull @Positive Integer quantity,
@Size(max = 1000) String reason
) {}

com.stock.application.dto.response.\* :
public record CategoryDto(UUID id, String name, String description, Instant createdAt) {}

public record ProductDto(UUID id, String sku, String name, UUID categoryId,
String categoryName, BigDecimal price, Integer quantity,
Integer alertThreshold, Instant createdAt, Instant updatedAt) {}

public record StockMovementDto(UUID id, UUID productId, String productSku,
StockMovementType type, Integer quantity, String reason,
String authorUsername, Instant movementDate) {}

public record PageResponse<T>(List<T> items, int page, int size,
long totalElements, int totalPages) {}

public record ErrorResponse(int status, String error, String message,
String path, Instant timestamp) {}

Mapping Entity → DTO :

Option A : MapStruct (@Mapper(componentModel = "spring"))
Option B : Constructeurs explicits (plus verbeux mais zéro dépendance) → Choisis et justifie.
Tests :

CreateCategoryCommand("") → @Valid lève ConstraintViolationException
ProductDto sérialise correctement en JSON (Jackson)
PageResponse<T> générique fonctionne

**Critère de « fini » :** DTOs définis comme records. `mvn test` passe. Validation Jakarta fonctionnelle. Mapping domaine↔DTO testé.

---

# PHASE 5 — Adapter Layer (Inbound HTTP + Outbound)

## Prompt 5.1 — Controllers HTTP (Adapter Inbound)

Implémente les contrôleurs REST Spring MVC.

Convention :

@RestController, un par aggregate
Injecte les UseCases (via constructeur)
N'appelle JAMAIS directement les repositories
N'appelle JAMAIS directement le domaine
Rend des DTOs (pas des entities)
Gère les codes HTTP (200, 201, 204, 400, 401, 403, 404)
com.stock.adapter.inbound.web.controller.CategoryController :
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
private final CreateCategoryUseCase createUseCase;
private final ListCategoriesUseCase listUseCase;
private final GetCategoryUseCase getUseCase;
private final DeleteCategoryUseCase deleteUseCase;

// Injecté via constructeur

@GetMapping
public ResponseEntity<List<CategoryDto>> list(
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "20") @Max(100) int size
) { ... }

@GetMapping("/{id}")
public ResponseEntity<CategoryDto> get(@PathVariable UUID id) { ... }

@PostMapping
@PreAuthorize("hasAnyRole('STOCK_MANAGER', 'STOCK_ADMIN')")
public ResponseEntity<CategoryDto> create(@Valid @RequestBody CreateCategoryCommand cmd) {
CategoryDto dto = createUseCase.execute(cmd);
URI location = URI.create("/api/v1/categories/" + dto.id());
return ResponseEntity.created(location).body(dto);
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('STOCK_ADMIN')")
public ResponseEntity<Void> delete(@PathVariable UUID id) { ... }
}

ProductController : idem +
@GetMapping("/low-stock") → produits sous seuil d'alerte
@PreAuthorize sur chaque méthode selon matrice

StockMovementController :
@PostMapping → @PreAuthorize("hasAnyRole('STOCK_MANAGER', 'STOCK_ADMIN')")
@GetMapping → @PreAuthorize("hasAnyRole('STOCK_VIEWER', 'STOCK_MANAGER', 'STOCK_ADMIN')")

com.stock.adapter.inbound.web.handler.GlobalExceptionHandler :
@RestControllerAdvice

InsufficientStockException → 400 + ErrorResponse
DuplicateSkuException → 409 + ErrorResponse
EntityNotFoundException → 404 + ErrorResponse
ValidationException → 400 + ErrorResponse
MethodArgumentNotValidException → 400 + details des violations
AccessDeniedException → 403
Exception (fallback) → 500 (log stack, message générique au client)
Matrice de rôles (à appliquer via @PreAuthorize ou SecurityFilterChain) :
| Endpoint | GET | POST | PUT | DELETE |
| /categories | VIEWER+ | MANAGER+ | MANAGER+ | ADMIN |
| /products | VIEWER+ | MANAGER+ | MANAGER+ | ADMIN |
| /products/low-stock | VIEWER+ | - | - | - |
| /stock-movements | VIEWER+ | MANAGER+ | - | ADMIN |

Tests (MockMvc, @WebMvcTest) :

GET /categories sans token → 401
GET /categories token VIEWER → 200
POST /categories token VIEWER → 403
POST /categories token MANAGER → 201
DELETE /categories/{id} token MANAGER → 403
DELETE /categories/{id} token ADMIN → 204
POST /products sku invalide → 400 + message
POST /products sku dupliqué → 409
Explique le flux complet : HTTP → Controller → UseCase → Domain/Ports → Response → JSON.
Explique pourquoi le contrôleur est "thin" (aucune logique métier).

**Critère de « fini » :** `mvn test` passe. Les tests MockMvc vérifient 401/403/200/201/204/400/404/409. Le GlobalExceptionHandler rend du JSON propre.

---

## Prompt 5.2 — Security Configuration (OAuth2 Resource Server)

Configure Spring Security pour validation JWT via Keycloak (JWKS).

com.stock.adapter.inbound.web.config.SecurityConfig :
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // pour @PreAuthorize
public class SecurityConfig {

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
http
.csrf(csrf -> csrf.disable()) // API REST, pas de forms
.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(auth -> auth
.requestMatchers("/actuator/health", "/actuator/info", "/openapi/", "/v3/api-docs/").permitAll()
.requestMatchers("/api/v1/\*\*").authenticated()
.anyRequest().denyAll()
)
.oauth2ResourceServer(oauth2 -> oauth2
.jwt(jwt -> jwt
.jwtAuthenticationConverter(new JwtAuthenticationConverter(
new RealmRoleConverter())) // mappe realm roles → Spring authorities
)
)
.exceptionHandling(ex -> ex
.authenticationEntryPoint((req, res, ex2) -> {
res.setStatus(401);
res.setContentType("application/json");
res.getWriter().write("{"error":"Unauthorized"}");
})
.accessDeniedHandler((req, res, ex2) -> {
res.setStatus(403);
res.setContentType("application/json");
res.getWriter().write("{"error":"Forbidden"}");
})
);
return http.build();
}

@Bean
public JwtDecoder jwtDecoder(
@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri
) {
// Spring Boot auto-configure le JwtDecoder depuis l'issuer-uri (JWKS)
// Mais on peut customiser : leeway, claims mapper, etc.
NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwksUri(
issuerUri.replace("/.well-known/openid-configuration",
"/protocol/openid-connect/certs")
).build();
decoder.setJwsAlgorithmVerifier(new RSASSAVerifier(...)); // ou auto
return decoder;
}
}

com.stock.adapter.outbound.security.RealmRoleConverter
(implements Converter<Jwt, AbstractAuthenticationToken>) :

Extrait le claim "roles" (array de realm roles)
Mappe "STOCK_ADMIN" → new SimpleGrantedAuthority("ROLE_STOCK_ADMIN")
Extrait preferred_username → name
Extrait email → detail
Rend un JwtAuthenticationToken
com.stock.adapter.outbound.security.TokenClaimsAdapter
(implémente AuthGatewayPort du domaine) :

Extrait les claims depuis SecurityContext (Jwt)
Construit un TokenClaims record du domaine
Rend les roles comme Set<Role> (enum du domaine)
Tests (Spring Security Test) :

Token avec role STOCK_ADMIN → @PreAuthorize("hasRole('STOCK_ADMIN')") passe
Token avec role STOCK_VIEWER → @PreAuthorize("hasRole('STOCK_ADMIN')") → 403
Pas de token → 401
Token expiré → 401
Token signé avec mauvaise clé → 401
Utilisation de @WithMockJwt ou JwtTestUtils en test.

Explique :

Pourquoi resource-server (pas client) : l'API ne fait que VALIDER des tokens, pas les émettre. Keycloak émet, Spring valide.
Comment le JWKS est résolu (issuer-uri → Spring fetch les clés publiques)
Pourquoi on mappe realm roles → authorities Spring
Le leeway et la rotation de clés (Keycloak fait auto-rotation, Spring cache JWKS)

**Critère de « fini » :** `mvn test` passe. Token valide Keycloak → 200. Token sans role → 403. Pas de token → 401. Token expiré → 401. Le domaine reçoit un TokenClaims propre via AuthGatewayPort.

---

## Prompt 5.3 — Persistence Adapter (Spring Data JPA + Mapping)

Implémente la persistance — l'adapter outbound qui implémente les ports du domaine.

STRATÉGIE : Double mapping (Domaine ↔ JPA)

Le CORE DOMAIN a ses entities (pur Java, sans @Entity)
L'ADAPTER a des JPA entities (@Entity) qui mappent le schéma SQL
Un MAPPER convertit entre les deux
Le Spring Data Repository (interface + impl JPA) est un détail interne de l'adapter
Le use case ne voit QUE le port du domaine
com.stock.adapter.outbound.persistence.entity.\* (JPA entities) :
@Entity
@Table(name = "categories")
public class CategoryJpaEntity {
@Id
private UUID id;
@Column(name = "name", unique = true, nullable = false)
private String name;
@Column(name = "description")
private String description;
@Column(name = "created_at", nullable = false)
private Instant createdAt;
// getters/setters (ou record si Hibernate 6 supporte, mais mutable est plus sûr pour JPA)
}

@Entity
@Table(name = "products")
public class ProductJpaEntity { ... }

@Entity
@Table(name = "stock_movements")
public class StockMovementJpaEntity { ... }

com.stock.adapter.outbound.persistence.mapper.\* :
// Option A : MapStruct
@Mapper(componentModel = "spring")
public interface CategoryMapper {
CategoryJpaEntity toJpa(Category domain);
Category toDomain(CategoryJpaEntity jpa);
}

// Option B : Mapper manuel (classe avec méthodes toJpa/toDomain)
→ Choisis MapStruct (moins de boilerplate) et justifie.

com.stock.adapter.outbound.persistence.repository.\* (Spring Data, interne) :
@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {
Optional<CategoryJpaEntity> findByName(String name);
boolean existsByName(String name);
}

// Idem pour Product, StockMovement

com.stock.adapter.outbound.persistence.adapter.\* (implémente les ports du domaine) :
@Repository // ou @Component
public class JpaCategoryRepositoryAdapter implements CategoryRepositoryPort {
private final CategoryJpaRepository jpaRepo;
private final CategoryMapper mapper;

@Override
public List<Category> findAll() {
return jpaRepo.findAll().stream()
.map(mapper::toDomain)
.toList();
}

@Override
public Optional<Category> findById(UUID id) {
return jpaRepo.findById(id).map(mapper::toDomain);
}

@Override
public Category save(Category category) {
CategoryJpaEntity jpa = mapper.toJpa(category);
CategoryJpaEntity saved = jpaRepo.save(jpa);
return mapper.toDomain(saved);
}

@Override
public void delete(UUID id) {
jpaRepo.deleteById(id);
}
}

// Idem pour Product, StockMovement

Transactions :

@Transactional sur les ADAPTERS (pas sur les use cases)
Ou @Transactional sur les use cases si multi-repo → Pour RecordStockMovementUseCase (2 writes : movement + product) : @Transactional sur le use case (justifié : unité de travail métier)
Gestion des exceptions SQL :

DataIntegrityViolationException (unique constraint) → DuplicateSkuException
OptimisticLockException → ConflictException
Wrap dans les adapters, pas dans le domaine
Tests d'intégration (@DataJpaTest + Testcontainers PostgreSQL) :

@Testcontainers
@Container static PostgreSQLContainer postgres = ...
@DynamicPropertySource pour configurer datasource
Test CRUD complet via le PORT (pas via le JPA repo directement)
Test : save produit avec SKU dupliqué → DataIntegrityViolation → DuplicateSkuException
Test : delete catégorie cascade → produits orphelins supprimés
Explique :

Pourquoi double mapping (domaine pur vs JPA) : isolation, testabilité, le domaine ne dépend pas de Hibernate
Pourquoi les adapters implémentent les ports : inversion de dépendance
Pourquoi @Transactional est ici (ou sur use case) : le domaine ne connaît pas les transactions

**Critère de « fini » :** `mvn test` passe. Tests intégration Testcontainers : CRUD complet via port. Contrainte unique → DuplicateSkuException. Cascade delete fonctionne. Le domaine est INCONSCIENT de JPA.

---

## Prompt 5.4 — Event Publisher Adapter

Implémente la publication d'événements.

com.stock.adapter.outbound.event.InMemoryEventPublisher
implements EventPublisherPort :

@Component
Stocke les events en mémoire (List<DomainEvent>) pour la démo
publish(event) → ajoute à la liste + log
future : remplacer par Spring Event, Kafka, ou database outbox
com.stock.adapter.outbound.event.SpringEventPublisher (optionnel, pour démo) :

Utilise ApplicationEventPublisher de Spring
Permet d'écouter les events (ex. : envoyer un email, mettre à jour un cache)
@EventListener sur les types d'events
com.stock.adapter.outbound.event.OutboxEventPublisher (prod-ready, optionnel) :

Écrit l'event dans une table outbox_events
Un worker async publie vers Kafka/MQ
Garantit at-least-once delivery
→ Pour la démo, InMemoryEventPublisher suffit. Explique l'outbox pattern pour prod.

Tests :

publish() stocke l'event
Un listener @EventListener réagit à ProductCreatedEvent
InMemory : après 3 events, la liste contient 3 éléments

**Critère de « fini » :** `mvn test` passe. Events sont capturés. Le domaine émet, l'infra publie — découplage vérifié.

---

## Prompt 5.5 — Clock Adapter

Implémente ClockPort.

com.stock.adapter.outbound.infrastructure.SystemClock implements ClockPort :
@Component
public Instant now() { return Instant.now(); }

com.stock.adapter.outbound.infrastructure.FixedClock implements ClockPort :
(pour tests uniquement)
public Instant now() { return fixedInstant; }

Configuration : @Bean SystemClock dans un @Configuration

Tests : injecter FixedClock → vérifier que les timestamps sont déterministes

**Critère de « fini » :** Tests déterministes (pas de flaky tests liées aux timestamps).

---

# PHASE 6 — Infrastructure Layer & Configuration

## Prompt 6.1 — Configuration, Bean Wiring, Health

Construis l'infrastructure Spring Boot :

com.stock.infrastructure.config.AppProperties :
@ConfigurationProperties(prefix = "stock")
public record AppProperties(
String env,
int maxPageSize,
int payloadMaxKb
) {}
// Relié via @EnableConfigurationProperties

com.stock.infrastructure.config.BeanFactory :
@Configuration
public class BeanFactory {
// Registre les adapters (ports implémentations)
// Si MapStruct : @Bean mappers
// Si Spring Data auto-config : rien à faire (auto-detection)
// @Bean ClockPort → SystemClock
// @Bean EventPublisherPort → InMemoryEventPublisher
}

com.stock.infrastructure.health.DatabaseHealthIndicator :
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
// Vérifie qu'une requête SQL simple passe
// Rend Health.up() ou Health.down()
}

com.stock.infrastructure.health.KeycloakHealthIndicator :
@Component
public class KeycloakHealthIndicator implements HealthIndicator {
// HTTP GET vers JWKS URI (timeout 2s)
// Rend Health.up("keycloak", "JWKS accessible") ou degraded
}

/actuator/health expose :
{
"status": "UP",
"components": {
"db": { "status": "UP" },
"keycloak": { "status": "UP" },
"diskSpace": { "status": "UP" }
}
}

GET /actuator/info :
{
"app": "stock-api",
"version": "
g
i
t
.
c
o
m
m
i
t
.
i
d
.
d
e
s
c
r
i
b
e
"
,
"
e
n
v
"
:
"
git.commit.id.describe","env":"{spring.profiles.active}"
}

Security : /actuator/health et /actuator/info → permitAll
/actuator/\* autres → ADMIN only (ou désactivés)

Compress + headers sécurité :

X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'
Supprimer X-Powered-By, Server header
Tests :

/actuator/health → 200 UP
/actuator/health si DB down → 503
Headers sécurité présents dans la réponse

**Critère de « fini » :** `/actuator/health` → `{"status":"UP","components":{"db":{"status":"UP"},"keycloak":{"status":"UP"}}}`. Headers sécurité présents.

---

## Prompt 6.2 — Virtual Threads (Java 21)

Active les virtual threads pour le serveur web.

application.yml :
spring:
threads:
virtual:
enabled: true

Explication :

Spring Boot 3.2+ supporte virtual threads nativement
Chaque requête HTTP = 1 virtual thread (pas de pool bloquant)
Gain : scalabilité (10 000 connexions concurrentes avec ~200 CPU threads)
Limites : pas de gain si le bottleneck est I/O bloquant dans du code non-managé
Pour cette API (I/O DB + I/O HTTP), c'est un gain direct
Vérification :

Au log, les threads s'appellent "virtual-1", "virtual-2" (pas "http-nio-8080-exec-1")
ou au log Spring : "Using Executor: 'applicationTaskExecutor' with virtual threads"
Test de load (optionnel) :

k6 ou wrk : 1000 requêtes concurrentes → toutes répondent < 500ms
Avec thread pool classique : saturation à ~200 requêtes
Explique le trade-off et quand virtual threads ne sont pas bénéfiques.

**Critère de « fini » :** Virtual threads actifs au log. L'app scale au-delà du pool natif.

---

# PHASE 7 — Finition, robustesse et MCP

## Prompt 7.1 — OpenAPI / Swagger UI

Expose une spec OpenAPI 3.0 auto-générée.

springdoc-openapi-starter-webmvc-ui est déjà dans le pom.
Swagger UI accessible sur /swagger-ui.html (dev only).
Spec JSON sur /v3/api-docs.

com.stock.adapter.inbound.web.config.OpenApiConfig :
@Configuration
public class OpenApiConfig {
@Bean
public OpenAPI customOpenAPI() {
return new OpenAPI()
.info(new Info()
.title("Stock Management API")
.version("1.0.0")
.description("API de gestion de stock — Clean Architecture, Spring Boot, Keycloak")
)
.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
.components(new Components()
.addSecuritySchemes("bearerAuth", new SecurityScheme()
.name("Authorization")
.type(SecurityScheme.Type.HTTP)
.scheme("bearer")
.bearerFormat("JWT")
.description("Keycloak JWT (realm roles: STOCK_ADMIN, STOCK_MANAGER, STOCK_VIEWER)")
)
);
}
}

Annotations sur les controllers :
@Operation(summary = "Créer un produit", description = "...")
@ApiResponse(responseCode = "201", description = "Produit créé")
@ApiResponse(responseCode = "400", description = "Validation failed", content = ...)
@ApiResponse(responseCode = "409", description = "SKU already exists")
@ApiResponse(responseCode = "403", description = "Insufficient role")

Restriction en prod :
@Profile("dev") sur le Bean SwaggerUiConfig
ou springdoc.api-docs.enabled=false en prod

Endpoint /v3/api-docs → permitAll (pour que les devs front et le MCP puissent
lire la spec sans token)

Explique pourquoi OpenAPI auto-généré (springdoc) > maintenu à la main :
zero drift, toujours à jour avec le code.

**Critère de « fini » :** `curl http://localhost:8080/v3/api-docs` → JSON OpenAPI valide. Swagger UI affiche tous les endpoints avec sécu JWT.

---

## Prompt 7.2 — Logging, Error Handling, Configuration

Finalise l'exploitation :

Logback (logback-spring.xml) :

Pattern JSON en prod (logstash-logback-encoder)
Pattern lisible en dev
File + Console
Async appender (performance)
Niveaux : com.stock=DEBUG (dev), com.stock=WARN (prod)
org.springframework.security=INFO (pas DEBUG = tokens dans les logs)
Corrélation :

MDC : traceId (UUID par requête)
LogFilter : ajoute traceId au MDC, le retire après
Chaque log contient le traceId → traçabilité bout-en-bout
Error handling (déjà fait en 5.1, mais finalise) :

En prod : include-stacktrace=never, include-message=always
Le stack trace est en LOG uniquement, jamais dans la réponse HTTP
ErrorResponse ne contient que : status, error, message (sémantique), path
Rate limiting (optionnel, pour prod) :

Bucket4j ou Resilience4j RateLimiter
100 req/min par IP sur /api/v1/\*\*
429 si dépassé
Payload size limit :
server:
tomcat:
max-http-form-post-size: 1MB
max-http-header-size: 16KB
spring:
servlet:
multipart:
max-file-size: 5MB

.gitignore strict : .env, target/, \*.log, .idea/

Profiles :

dev : logs DEBUG, Swagger UI activé, H2 optionnel
test : logs WARN, Testcontainers
prod : logs WARN+, Swagger désactivé, virtual threads, OPcache (non applicable Java)
Explique la stratégie de logging (structured, correlated, sans secrets).

**Critère de « fini » :** Logs JSON structurés avec traceId. Pas de token dans les logs. Pas de stack trace en prod. `/actuator/loggers` permet de changer le niveau au runtime.

---

## Prompt 7.3 — Tests d'intégration bout-en-bout (Testcontainers)

Tests d'intégration réalistes (Testcontainers PostgreSQL + Keycloak mock ou réel).

Setup :

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Container PostgreSQLContainer (postgres:16-alpine)
@DynamicPropertySource → configure datasource + flyway
Tokens : générés LOCALEMENT (HS256 ou RS256 avec clé privée de test) pour éviter la dépendance Keycloak en test → rapide
Ou : @Container KeycloakContainer (quarkus) si on veut le flow réel
Scénario 1 : CRUD normal

POST /api/v1/categories (token ADMIN) → 201 + Location
POST /api/v1/products (token MANAGER) → 201
GET /api/v1/products (token VIEWER) → 200 + list
GET /api/v1/products/{id} → 200
DELETE /api/v1/products/{id} (token ADMIN) → 204
GET /api/v1/products/{id} → 404
Scénario 2 : Stock et mouvements

Créer produit qty=100, alertThreshold=20
POST /api/v1/stock-movements ENTRY +50 → 201, product.qty=150
POST /api/v1/stock-movements EXIT -100 → 201, product.qty=50
POST /api/v1/stock-movements EXIT -100 (qty 50 < 100) → 400 {"error":"InsufficientStock","message":"Available: 50, requested: 100"}
GET /api/v1/stock-movements?productId=X → 200 + 2 mouvements
GET /api/v1/products/low-stock → le produit n'y est pas (50 > 20)
Faire un EXIT -35 → qty=15 ≤ 20 → AlertThresholdExceededEvent émit
GET /api/v1/products/low-stock → le produit y est
Scénario 3 : Permissions (matrice complète)

VIEWER : GET tout (200), POST/PUT/DELETE (403)
MANAGER : GET (200), POST/PUT (200), DELETE (403)
ADMIN : tout (200/201/204)
Pas de token : tout → 401
Scénario 4 : Validation

POST /api/v1/products sans sku → 400
POST /api/v1/products sku="AB" (trop court) → 400
POST /api/v1/products price=-5 → 400
POST /api/v1/products category_id inconnu → 404
Exécution :

mvn verify → tous les tests passent en < 30s
Testcontainers démarre PostgreSQL en ~5s, Keycloak mock = 0s
Coverage : JaCoCo → > 80% sur core.domain, > 70% sur application

Exporte le scénario 2 comme un test d'acceptance lisible (nom descriptive,
étapes commentées).

**Critère de « fini » :** `mvn verify` passe. Tous les scénarios couverts. Base Testcontainers propre entre tests. Tokens signés localement = tests rapides.

---

## Prompt 7.4 — Revue de sécurité finale

Revue de sécurité complète avant livraison :

JWT / OAuth2 :

Validation RS256 via JWKS (spring-security-oauth2-resource-server)
issuer-uri vérifié (Keycloak)
exp, nbf vérifiés (leeway 30s)
alg=none → rejeté (Nimbus JWT le fait nativement)
aud/azp : vérifier que "stock-api" est bien dans l'audience
Rotation de clés Keycloak : Spring refetch le JWKS automatiquement
Secrets :

DB password, Keycloak client secret → dans .env / vault, JAMAIS en code
.gitignore : .env, _.pem, _.key
Spring Cloud Config ou HashiCorp Vault en prod (mention dans ADR)
SQL Injection :

Spring Data JPA → requêtes préparées nativement (parameterized)
Pas de native queries avec string concat
Vérifier qu'aucune @Query ne concatène des variables
Autorisation :

Matrice endpoint/rôle testée (scénario 3 ci-dessus)
@PreAuthorize sur chaque méthode (pas de fail-open)
Default : denyAll (security config)
Fuites d'info :

Erreurs 500 : message générique, stack trace en log uniquement
Pas de version Spring, pas de version Java dans les headers
Server header supprimé ou générique
Pas de token dans les logs (vérifier logback)
Error page : pas de trace, pas de détails internals
DoS :

Rate limiting (429)
Payload size limit (413)
Timeout HTTP (30s)
Connection pool borné (HikariCP max=10)
CORS :

Si front-end en localhost:3000 : allowedOrigins: http://localhost:3000
Pas de \* (wildcard)
allowedMethods: GET, POST, PUT, DELETE
allowedHeaders: Authorization, Content-Type
Pas de credentials + wildcard
Transport :

HTTPS obligatoire en prod (proxy TLS : nginx/traefik)
HSTS header
TLS 1.2+
Runtime :

JVM : -Xmx512m -Xms256m (borné)
GC : ZGC ou G1 (défaut Java 21)
Virtual threads : activé
Pas de debug agent (JDWP) en prod
Supply chain :

OWASP dependency-check (mvn dependency-check:check)
Aucune dépendance with known CVE
Versions pinnées (pas de ranges ouvertes)
Liste les findings par gravité (Critical / High / Medium / Low) + correction.
Format : une ligne = finding + gravité + fix.

**Critère de « fini » :** Tous les findings Critical/High corrigés. `mvn dependency-check:check` → 0 CVE High+. Headers sécurité vérifiés. Checklist dans `docs/SECURITY.md`.

---

## Prompt 7.5 — Serveur MCP pour développeurs front-end

Crée un serveur MCP qui expose l'API REST aux outils IA / front-end.

Objectif : un dev front-end (ou une IA) peut interroger l'API en langage naturel,
sans gérer manuellement les tokens OAuth2.

Architecture :
mcp/
├── server/
│ ├── index.js (ou main.py)
│ ├── tools/
│ │ ├── categories.js
│ │ ├── products.js
│ │ └── stock-movements.js
│ ├── auth.js (gère le JWT)
│ └── config.js
├── package.json (ou requirements.txt)
├── mcp.json (config Claude Desktop)
└── README.md

Tools exposés (chacun = une fonction MCP) :

list_categories(page, size) → GET /api/v1/categories
get_category(id) → GET /api/v1/categories/{id}
create_category(name, description) → POST /api/v1/categories
delete_category(id) → DELETE /api/v1/categories/{id}
list_products(page, size, categoryId?, lowStock?) → GET /api/v1/products
get_product(id) → GET /api/v1/products/{id}
create_product(sku, name, categoryId, price, quantity, alertThreshold)
record_stock_movement(productId, type, quantity, reason)
list_stock_movements(page, size, productId?)
get_health() → GET /actuator/health
Authentification :

Env var : MCP_JWT_TOKEN (token Bearer, obtenu via Keycloak token endpoint)
Ou : MCP_CLIENT_ID + MCP_CLIENT_SECRET + MCP_USERNAME + MCP_PASSWORD → le serveur fait le token exchange lui-même (plus pratique)
Chaque tool injecte Authorization: Bearer <token>
Configuration (mcp/mcp.json) :
{
"mcpServers": {
"stock-api": {
"command": "node",
"args": ["./mcp/server/index.js"],
"env": {
"BACKEND_URL": "http://localhost:8080",
"MCP_JWT_TOKEN": "eyJhbG...",
"DEFAULT_ROLE": "STOCK_ADMIN"
}
}
}
}

Implémentation (Node.js) :

@modelcontextprotocol/sdk (SDK officiel)
fetch (natif Node 18+) pour les appels HTTP
Chaque tool : name, description, inputSchema (JSON Schema), handler
Le handler fait le fetch, parse la réponse, rend le texte formaté
Exemple d'invocation via Claude :
Utilisateur : "Crée un produit SKU=ABC-001, nom=Café Arabica,
catégorie 1, prix 12.50, quantité 200, seuil 50"
Claude → appelle create_product(...) → rend le JSON de réponse

Tests (optionnels) :

Mock le backend (msw ou nock)
Vérifie que chaque tool envoie la bonne requête HTTP
Vérifie le format de réponse
README.md :

Installation : npm install
Configuration : obtenir le token, éditer mcp.json
Intégration Claude Desktop / Claude Code
Liste des tools avec exemples
Troubleshooting (token expiré → re-login)
Explique pourquoi un serveur MCP décuple l'agilité :

Un dev front peut "parler" à l'API sans curl, sans Postman, sans token manuel
Une IA peut générer du code front en ayant "vue" les réponses réelles
La spec OpenAPI + MCP = deux sources de vérité complémentaires

**Critère de « fini » :** Serveur MCP démarre (`node mcp/server/index.js`), expose les tools dans Claude Desktop. Une invocation naturelle rend la réponse JSON du backend. Token géré transparent
