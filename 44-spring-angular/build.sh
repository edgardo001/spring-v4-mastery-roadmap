#!/usr/bin/env bash
# Modulo 44 - Build script para Git Bash / Linux / macOS.
# Compila SOLO el backend. El frontend se compila con `ng build` (ver frontend/README.md).
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROADMAP_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

export JAVA_HOME="$ROADMAP_ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

MVN="$ROADMAP_ROOT/apache-maven-3.9.16/bin/mvn"

cd "$SCRIPT_DIR/backend"
"$MVN" clean verify

echo ""
echo "OK: artefacto en 44-spring-angular/backend/target/spring-angular-1.0.0.jar"
