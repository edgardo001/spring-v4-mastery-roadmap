#!/usr/bin/env bash
# Build script (Git Bash / Linux / macOS) para el módulo 07-jpa-hibernate.
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
echo "Artefacto: target/jpa-hibernate-1.0.0.jar"
echo "Ejecutar:  java -jar target/jpa-hibernate-1.0.0.jar"
