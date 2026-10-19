#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

if [[ ! -f .env ]]; then
    echo "Missing .env in the repository root. Configure it using .env.example before deploying." >&2
    exit 1
fi

compose=(docker compose --env-file .env -f docker-compose.yml)
# Validate without printing credentials, then build before replacing the app.
"${compose[@]}" config --quiet
"${compose[@]}" build --pull semprotdb
"${compose[@]}" up -d --no-deps --wait --wait-timeout 300 semprotdb
