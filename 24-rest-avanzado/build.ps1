# Build script (PowerShell) para el módulo 24-rest-avanzado.
$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RoadmapRoot = Split-Path -Parent $ScriptDir

$env:JAVA_HOME = Join-Path $RoadmapRoot "jdk-21.0.11+10"
$env:PATH = "$($env:JAVA_HOME)\bin;$env:PATH"

$Mvn = Join-Path $RoadmapRoot "apache-maven-3.9.16\bin\mvn.cmd"
if (-not (Test-Path $Mvn)) { $Mvn = "mvn" }

Push-Location $ScriptDir
try {
    & $Mvn -q clean package
    Write-Host ""
    Write-Host "Artefacto: target/rest-avanzado-1.0.0.jar"
    Write-Host "Ejecutar:  java -jar target/rest-avanzado-1.0.0.jar"
} finally {
    Pop-Location
}
