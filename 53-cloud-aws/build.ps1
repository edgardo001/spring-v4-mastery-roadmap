# build.ps1 — Build del módulo 53 con toolchain portable (PowerShell).
$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir   = Split-Path -Parent $ScriptDir

$env:JAVA_HOME = Join-Path $RootDir "jdk-21.0.11+10"
$env:PATH      = "$($env:JAVA_HOME)\bin;$env:PATH"
$Mvn           = Join-Path $RootDir "apache-maven-3.9.16\bin\mvn.cmd"

Push-Location $ScriptDir
try {
    & $Mvn -q clean package
    Write-Host "Artefacto: $ScriptDir\target\cloud-aws-1.0.0.jar"
    Write-Host "Ejecutar con: java -jar target\cloud-aws-1.0.0.jar"
} finally {
    Pop-Location
}
