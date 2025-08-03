#!/usr/bin/env bash
set -e

# Usage: ./scripts/start.sh [local|dev|prod]

if [ "$#" -ne 1 ]; then
   echo "Usage: $0 [local|dev|prod]" >&2
   exit 1
fi

ENV=$1

case "$ENV" in
    local)
        COMPOSE_FILE="-f docker-compose.yml -f docker-compose.local.yml"
        ;;
    dev)
        COMPOSE_FILE="-f docker-compose.yml -f docker-compose.dev.yml"
        ;;
    prod)
        COMPOSE_FILE="-f docker-compose.yml -f docker-compose.prod.yml"
        ;;
    *)
        echo "Invalid environment: $ENV" >&2
        exit 1
esac

docker-compose $COMPOSE_FILE up --build -d