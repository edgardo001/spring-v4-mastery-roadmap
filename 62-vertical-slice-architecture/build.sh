#!/usr/bin/env bash
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

JAR="target/vertical-slice-architecture-1.0.0.jar"
[[ -f "$JAR" ]] || { echo "ERROR: $JAR no existe"; exit 1; }
echo "OK: $JAR listo. Ejecutar con:  java -jar $JAR"
