#!/usr/bin/env bash
# Script de build para Git Bash (Linux/Mac/Windows).
# Usa el JDK 21 portable y Maven portable ubicados en la raiz del roadmap.

set -e

ROADMAP_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export JAVA_HOME="$ROADMAP_ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

MVN="$ROADMAP_ROOT/apache-maven-3.9.16/bin/mvn"

echo "== JDK =="
"$JAVA_HOME/bin/java" -version

echo "== mvn clean package =="
"$MVN" -f "$(dirname "$0")/pom.xml" clean package

echo "== Artefacto generado =="
ls -lh "$(dirname "$0")/target/jwt-1.0.0.jar"

echo "Para ejecutar: java -jar target/jwt-1.0.0.jar"
