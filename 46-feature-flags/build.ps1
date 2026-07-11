# build.ps1 - Módulo 46 Feature Flags (PowerShell)
$ErrorActionPreference = "Stop"

$RoadmapRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$env:JAVA_HOME = Join-Path $RoadmapRoot "jdk-21.0.11+10"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

$Mvn = Join-Path $RoadmapRoot "apache-maven-3.9.16\bin\mvn.cmd"

& $Mvn -q clean package
Write-Host "Artifact: target/feature-flags-1.0.0.jar"
