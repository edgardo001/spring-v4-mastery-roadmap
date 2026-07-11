#!/usr/bin/env bash
# Build + test + package del modulo 26.
# Uso: ./build.sh  (desde 26-docker/)
#
# NOTA: este script SOLO produce el fat-JAR target/docker-1.0.0.jar.
# NO ejecuta "docker build" porque el demonio Docker puede no estar
# corriendo. Para construir la imagen, ver README.md ("Como ejecutar").
set -euo pipefail

# Raiz del roadmap (carpeta padre de este modulo).
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

# Forzar el JDK 21 portable (el sistema del desarrollador tiene Java 17).
export JAVA_HOME="$ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

# Maven portable: .cmd en Windows, script sh en Unix.
if [[ -x "$ROOT/apache-maven-3.9.16/bin/mvn" ]]; then
    MVN="$ROOT/apache-maven-3.9.16/bin/mvn"
else
    MVN="$ROOT/apache-maven-3.9.16/bin/mvn.cmd"
fi

cd "$(dirname "$0")"
"$MVN" clean verify

JAR="target/docker-1.0.0.jar"
[[ -f "$JAR" ]] || { echo "ERROR: $JAR no existe"; exit 1; }
echo "OK: $JAR listo. Ejecutar con:  java -jar $JAR"
echo "Para construir la imagen Docker (requiere Docker Desktop corriendo):"
echo "  docker build -t docker-demo ."
echo "  docker run -p 8080:8080 docker-demo"
