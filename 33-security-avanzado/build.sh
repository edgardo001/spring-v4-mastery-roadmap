#!/usr/bin/env bash
# Build script (Git Bash) - modulo 33 security-avanzado
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

export JAVA_HOME="$ROOT_DIR/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

MVN="$ROOT_DIR/apache-maven-3.9.16/bin/mvn"

cd "$SCRIPT_DIR"
"$MVN" clean package "$@"
echo "Artefacto: $SCRIPT_DIR/target/security-avanzado-1.0.0.jar"
