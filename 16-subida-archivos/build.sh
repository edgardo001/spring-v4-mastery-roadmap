#!/usr/bin/env bash
# Build + test + package del módulo 16.
# Uso: ./build.sh  (desde 16-subida-archivos/)
set -euo pipefail

# Raíz del roadmap (carpeta padre de este módulo).
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

# Forzar el JDK 21 portable (el sistema tiene Java 17).
export JAVA_HOME="$ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

# Maven portable (script sh en Unix, .cmd en Windows).
if [[ -x "$ROOT/apache-maven-3.9.16/bin/mvn" ]]; then
    MVN="$ROOT/apache-maven-3.9.16/bin/mvn"
else
    MVN="$ROOT/apache-maven-3.9.16/bin/mvn.cmd"
fi

cd "$(dirname "$0")"
"$MVN" clean verify

JAR="target/subida-archivos-1.0.0.jar"
[[ -f "$JAR" ]] || { echo "ERROR: $JAR no existe"; exit 1; }
echo "OK: $JAR listo. Ejecutar con:  java -jar $JAR"
