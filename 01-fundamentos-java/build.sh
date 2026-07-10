#!/usr/bin/env bash
# Build + test + package -> target/fundamentos-java-1.0.0.jar
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JDK="$ROOT/jdk-21.0.11+10/bin"
NAME="fundamentos-java-1.0.0"

cd "$(dirname "$0")"
rm -rf out target
mkdir -p out target

"$JDK/javac.exe" -d out src/records/*.java src/streams/*.java src/optional/*.java src/Main.java
"$JDK/jar.exe" --create --file "target/$NAME.jar" --main-class=Main -C out .

echo "== Running tests =="
"$JDK/java.exe" -jar "target/$NAME.jar"

echo "== Artifact =="
ls -l "target/$NAME.jar"
