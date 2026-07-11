# Build script PowerShell - modulo 40 event-driven
$ErrorActionPreference = "Stop"
$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$env:JAVA_HOME = Join-Path $root "jdk-21.0.11+10"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$mvn = Join-Path $root "apache-maven-3.9.16\bin\mvn.cmd"

Push-Location $PSScriptRoot
try {
    & $mvn -q clean package
    Write-Host "Artefacto: target/event-driven-1.0.0.jar"
} finally {
    Pop-Location
}
