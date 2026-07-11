#!/usr/bin/env bash
# Build script Git Bash — módulo 40 event-driven
# Usa el JDK 21 portable de la raíz del roadmap.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export JAVA_HOME="$ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"
MVN="$ROOT/apache-maven-3.9.16/bin/mvn"

cd "$(dirname "$0")"
"$MVN" -q clean package
echo "Artefacto: target/event-driven-1.0.0.jar"
