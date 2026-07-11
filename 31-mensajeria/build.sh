#!/usr/bin/env bash
# Script de build para Git Bash / Linux / macOS.
# Usa el toolchain PORTABLE (JDK 21 + Maven 3.9.16) que vive en la raíz del roadmap.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export JAVA_HOME="$ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

if [[ -x "$ROOT/apache-maven-3.9.16/bin/mvn" ]]; then
    MVN="$ROOT/apache-maven-3.9.16/bin/mvn"
else
    MVN="$ROOT/apache-maven-3.9.16/bin/mvn.cmd"
fi

cd "$(dirname "$0")"
"$MVN" clean verify

JAR="target/mensajeria-1.0.0.jar"
[[ -f "$JAR" ]] || { echo "ERROR: $JAR no existe"; exit 1; }
echo "OK: $JAR listo. Ejecutar con:  java -jar $JAR"
