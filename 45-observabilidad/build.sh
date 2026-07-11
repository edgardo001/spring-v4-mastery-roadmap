#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

export JAVA_HOME="$ROOT_DIR/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

MVN="$ROOT_DIR/apache-maven-3.9.16/bin/mvn"
[ -x "$MVN" ] || MVN="$ROOT_DIR/apache-maven-3.9.16/bin/mvn.cmd"

cd "$SCRIPT_DIR"
"$MVN" clean verify

JAR="$SCRIPT_DIR/target/observabilidad-1.0.0.jar"
if [ ! -f "$JAR" ]; then
    echo "ERROR: $JAR no existe" >&2
    exit 1
fi
echo "OK: $JAR listo. Ejecutar con:  java -jar $JAR"
