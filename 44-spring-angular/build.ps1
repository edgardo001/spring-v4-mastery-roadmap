# Modulo 44 - Build script para PowerShell.
# Compila SOLO el backend. El frontend se compila con `ng build` (ver frontend/README.md).
$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RoadmapRoot = Split-Path -Parent $ScriptDir

$env:JAVA_HOME = Join-Path $RoadmapRoot "jdk-21.0.11+10"
$env:PATH = (Join-Path $env:JAVA_HOME "bin") + ";" + $env:PATH

$Mvn = Join-Path $RoadmapRoot "apache-maven-3.9.16\bin\mvn.cmd"

Set-Location (Join-Path $ScriptDir "backend")
& $Mvn clean verify
if ($LASTEXITCODE -ne 0) { throw "Maven build failed" }

Write-Output ""
Write-Output "OK: artefacto en 44-spring-angular\backend\target\spring-angular-1.0.0.jar"
