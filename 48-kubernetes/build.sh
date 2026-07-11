#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# JDK 21 portable + Maven portable
export JAVA_HOME="$ROOT_DIR/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"
MVN="$ROOT_DIR/apache-maven-3.9.16/bin/mvn"

cd "$SCRIPT_DIR"
"$MVN" clean verify

JAR="$SCRIPT_DIR/target/kubernetes-1.0.0.jar"
if [[ ! -f "$JAR" ]]; then
  echo "ERROR: $JAR no existe" >&2
  exit 1
fi

echo "OK: $JAR listo. Ejecutar con:  java -jar $JAR"
echo ""
echo "Siguientes pasos (fuera de este script):"
echo "  docker build -t kubernetes-demo:1.0.0 ."
echo "  kubectl apply -f k8s/"
echo "  # o alternativamente:  helm install kubernetes-demo ./helm"
