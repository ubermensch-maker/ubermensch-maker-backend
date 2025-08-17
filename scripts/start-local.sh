#!/usr/bin/env bash
set -e

# Local development script with hot reload
# Usage: ./scripts/start-local.sh

echo "Starting local development server with hot reload..."

# Export environment variables from .env.local
if [ -f .env.local ]; then
    export $(cat .env.local | grep -v '^#' | xargs)
    echo "Loaded environment variables from .env.local"
else
    echo "Warning: .env.local file not found"
fi

# Run Spring Boot with local profile and devtools
./gradlew bootRun --args='--spring.profiles.active=local'