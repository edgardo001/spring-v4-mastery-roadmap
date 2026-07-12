# Build script (PowerShell) — módulo 52 seguridad-owasp
$ErrorActionPreference = 'Stop'

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir   = Split-Path -Parent $ScriptDir

$env:JAVA_HOME = Join-Path $RootDir 'jdk-21.0.11+10'
$env:PATH      = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:PATH

$Mvn = Join-Path $RootDir 'apache-maven-3.9.16\bin\mvn.cmd'

Push-Location $ScriptDir
try {
    & $Mvn clean package @args
    Write-Host "Artefacto: $ScriptDir\target\seguridad-owasp-1.0.0.jar"
} finally {
    Pop-Location
}
