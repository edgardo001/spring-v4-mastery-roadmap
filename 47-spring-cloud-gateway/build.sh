#!/usr/bin/env bash
# build.sh - Módulo 47 Spring Cloud Gateway (Git Bash)
set -euo pipefail

ROADMAP_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export JAVA_HOME="$ROADMAP_ROOT/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"

MVN="$ROADMAP_ROOT/apache-maven-3.9.16/bin/mvn"

"$MVN" -q clean package
echo "Artifact: target/spring-cloud-gateway-1.0.0.jar"
