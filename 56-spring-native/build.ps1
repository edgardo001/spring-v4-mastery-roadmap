# Build script (PowerShell) para el modulo 56-spring-native.
# NOTA: Solo compila JAR normal. Para binario nativo usar: mvn -Pnative native:compile (requiere GraalVM).
$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RoadmapRoot = Split-Path -Parent $ScriptDir

$env:JAVA_HOME = Join-Path $RoadmapRoot "jdk-21.0.11+10"
$env:PATH = "$($env:JAVA_HOME)\bin;$env:PATH"

$Mvn = Join-Path $RoadmapRoot "apache-maven-3.9.16\bin\mvn.cmd"
if (-not (Test-Path $Mvn)) { $Mvn = "mvn" }

Push-Location $ScriptDir
try {
    & $Mvn -q clean verify
    Write-Host ""
    Write-Host "Artefacto: target/spring-native-1.0.0.jar"
    Write-Host "Ejecutar:  java -jar target/spring-native-1.0.0.jar"
    Write-Host "Nativo:    mvn -Pnative native:compile  (requiere GraalVM 21+)"
} finally {
    Pop-Location
}
