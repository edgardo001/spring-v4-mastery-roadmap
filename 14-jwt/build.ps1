# Script de build para PowerShell (Windows).
# Usa el JDK 21 portable y Maven portable ubicados en la raiz del roadmap.

$ErrorActionPreference = "Stop"

$ModuleDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$RoadmapRoot = Split-Path -Parent $ModuleDir

$env:JAVA_HOME = Join-Path $RoadmapRoot "jdk-21.0.11+10"
$env:PATH      = "$env:JAVA_HOME\bin;$env:PATH"

$Mvn = Join-Path $RoadmapRoot "apache-maven-3.9.16\bin\mvn.cmd"

Write-Host "== JDK =="
& "$env:JAVA_HOME\bin\java.exe" -version

Write-Host "== mvn clean package =="
& $Mvn -f (Join-Path $ModuleDir "pom.xml") clean package

Write-Host "== Artefacto generado =="
Get-ChildItem (Join-Path $ModuleDir "target\jwt-1.0.0.jar")

Write-Host "Para ejecutar: java -jar target\jwt-1.0.0.jar"
