#!/usr/bin/env bash
# build.sh - Compila y empaqueta el modulo 61-rsocket usando el toolchain portable.
# Uso: ./build.sh
set -euo pipefail

# Raiz del roadmap (dos niveles arriba de este script).
ROADMAP_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

export JAVA_HOME="$ROADMAP_ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

MVN="$ROADMAP_ROOT/apache-maven-3.9.16/bin/mvn"

echo "==> JAVA_HOME = $JAVA_HOME"
echo "==> Usando Maven en $MVN"

"$MVN" -f "$(dirname "${BASH_SOURCE[0]}")/pom.xml" clean package

echo "==> Artefacto: $(dirname "${BASH_SOURCE[0]}")/target/rsocket-1.0.0.jar"
