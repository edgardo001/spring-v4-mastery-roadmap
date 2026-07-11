#!/usr/bin/env bash
# Build script Git Bash - modulo 49 gRPC
# Usa el JDK 21 y Maven portables de la raiz del roadmap.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export JAVA_HOME="$ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"
MVN="$ROOT/apache-maven-3.9.16/bin/mvn"

cd "$(dirname "$0")"
"$MVN" -q clean package
echo "Artefacto: target/grpc-1.0.0.jar"
