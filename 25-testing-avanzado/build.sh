#!/usr/bin/env bash
# Build script (Git Bash / Linux / macOS) para el módulo 25-testing-avanzado.
# Requiere: JDK 21 portable + Maven portable en la raíz del roadmap.
# Docker es OPCIONAL: si no está, los tests de Testcontainers se saltan automáticamente.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROADMAP_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

export JAVA_HOME="$ROADMAP_ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

MVN="$ROADMAP_ROOT/apache-maven-3.9.16/bin/mvn"
if [ ! -x "$MVN" ]; then
  MVN="mvn"
fi

cd "$SCRIPT_DIR"
"$MVN" -q clean package
echo ""
echo "Artefacto: target/testing-avanzado-1.0.0.jar"
echo "Ejecutar:  java -jar target/testing-avanzado-1.0.0.jar"
