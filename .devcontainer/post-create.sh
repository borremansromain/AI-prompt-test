#!/usr/bin/env bash
# Post-create (Prompt 1.2) : demarrer postgres + keycloak ET afficher la matrice d'environnement.
# Au choix deux chemins :
#   A) "docker compose up" sur l'hote (recommande, garanti) -> app/postgres/keycloak dans le compose
#   B) depuis ce dev container : lever postgres+keycloak dans dind puis lancer "mvn spring-boot:run" ici
set -euo pipefail

echo "==> Stock Management API — environnement de dev"
if command -v docker >/dev/null 2>&1; then
  echo "    docker CLI dispo. Demarrage postgres + keycloak..."
  docker compose up -d postgres keycloak || echo "    (compose indispo dans le container — lancer 'docker compose up' sur l'hote)"
else
  echo "    docker CLI absent dans le dev container."
  echo "    -> Demarrer sur l'hote : docker compose up   (puis 'mvn spring-boot:run' ici)"
fi

echo ""
echo "    URLs / identifiants de test"
echo "      App        http://localhost:8080  (health: /actuator/health)"
echo "      Postgres   localhost:5432  (stock_db / stock_user)"
echo "      Keycloak   http://localhost:8081  (admin / admin)"
echo "      Realm      stock-app"
echo "      Users      alice@stock.local/alice123  bob@stock.local/bob123  carol@stock.local/carol123"
echo "      (rôles complets ajoutés au Prompt 2.1)"
