#!/usr/bin/env bash
# Build script (Git Bash / Linux / macOS) para el modulo 56-spring-native.
# NOTA: Solo compila JAR normal. Para binario nativo usar: mvn -Pnative native:compile (requiere GraalVM).
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
"$MVN" -q clean verify
echo ""
echo "Artefacto: target/spring-native-1.0.0.jar"
echo "Ejecutar:  java -jar target/spring-native-1.0.0.jar"
echo "Nativo:    mvn -Pnative native:compile  (requiere GraalVM 21+)"
