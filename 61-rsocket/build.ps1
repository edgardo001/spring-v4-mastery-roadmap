# build.ps1 - Compila y empaqueta el modulo 61-rsocket (PowerShell).
# Uso: .\build.ps1
$ErrorActionPreference = "Stop"

$ModuleDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$RoadmapRoot = Split-Path -Parent $ModuleDir

$env:JAVA_HOME = Join-Path $RoadmapRoot "jdk-21.0.11+10"
$env:PATH      = "$env:JAVA_HOME\bin;$env:PATH"

$Mvn = Join-Path $RoadmapRoot "apache-maven-3.9.16\bin\mvn.cmd"

Write-Host "==> JAVA_HOME = $env:JAVA_HOME"
Write-Host "==> Usando Maven en $Mvn"

& $Mvn -f (Join-Path $ModuleDir "pom.xml") clean package
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "==> Artefacto: $ModuleDir\target\rsocket-1.0.0.jar"
