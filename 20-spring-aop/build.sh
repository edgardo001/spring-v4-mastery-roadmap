#!/usr/bin/env bash
# build.sh — Build del módulo 20 con toolchain portable (Git Bash).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

export JAVA_HOME="$ROOT_DIR/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"
MVN="$ROOT_DIR/apache-maven-3.9.16/bin/mvn"

cd "$SCRIPT_DIR"
"$MVN" -q clean package
echo "Artefacto: $SCRIPT_DIR/target/spring-aop-1.0.0.jar"
echo "Ejecutar con: java -jar target/spring-aop-1.0.0.jar"
