# Points positifs pour la sécurité

## Authentification et autorisation

- Keycloak est utilisé comme fournisseur central d'identité.
- L'API fonctionne comme un OAuth2 Resource Server et valide localement les JWT via JWKS.
- Les tokens utilisent `RS256` et ont une durée de vie courte de 5 minutes.
- Trois rôles métier sont définis : `STOCK_ADMIN`, `STOCK_MANAGER` et `STOCK_VIEWER`.
- Les permissions sont appliquées au niveau des méthodes avec `@PreAuthorize`.
- Les routes `/api/v1/**` exigent une authentification.
- Les routes inconnues sont refusées avec `denyAll()`.

## Contrôle des données

- Les entrées HTTP sont validées avec Jakarta Bean Validation : `@NotNull`, `@NotBlank`, `@Size`, `@Positive`, etc.
- La base PostgreSQL applique également des contraintes `NOT NULL`, `UNIQUE`, `CHECK` et des clés étrangères.
- Flyway versionne les migrations et Hibernate est configuré en `ddl-auto: validate`, ce qui évite que l'application modifie automatiquement le schéma.
- Le domaine est séparé de Spring et de JPA grâce à la Clean Architecture, ce qui limite le couplage et réduit les risques d'exposition directe des entités persistées.

## Réduction des fuites d'informations

- Les stacktraces ne sont pas renvoyées dans les réponses HTTP.
- Le niveau de logs Spring Security reste à `INFO`, ce qui évite normalement d'afficher les tokens.
- Les variables sensibles sont externalisables via des variables d'environnement.
- `.env`, les clés privées et les certificats sont exclus du dépôt.
- L'identité de l'utilisateur est conservée sur les mouvements de stock via `author_username`, ce qui fournit une forme de traçabilité métier.

## Qualité et prévention

- Checkstyle et SpotBugs sont exécutés pendant le build Maven.
- JaCoCo permet de suivre la couverture des tests.
- `spring-security-test` et Testcontainers PostgreSQL sont prévus pour tester la sécurité et l'intégration.
- L'inscription libre Keycloak est désactivée et les emails en doublon sont interdits.
- Le flux implicite OAuth2 est désactivé et PKCE avec `S256` est configuré pour le client front-end.

## Réserves pour la production

Ces points sont bons surtout pour l'environnement de développement. Les mots de passe `admin123`, `secret_dev_only` et `manager123` sont présents dans les fichiers de configuration. Il faut impérativement les remplacer par des secrets externes en production.

Swagger, les détails Actuator et le niveau de logs `DEBUG` doivent également être durcis hors développement.
