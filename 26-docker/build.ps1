$ErrorActionPreference = 'Stop'

# Raiz del roadmap (carpeta padre del modulo)
$Root = Resolve-Path (Join-Path $PSScriptRoot '..')

# JDK 21 portable + Maven portable
$env:JAVA_HOME = Join-Path $Root 'jdk-21.0.11+10'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$Mvn = Join-Path $Root 'apache-maven-3.9.16\bin\mvn.cmd'

Set-Location $PSScriptRoot
& $Mvn clean verify
if ($LASTEXITCODE -ne 0) { throw "mvn clean verify fallo" }

$Jar = Join-Path $PSScriptRoot 'target\docker-1.0.0.jar'
if (-not (Test-Path $Jar)) { throw "ERROR: $Jar no existe" }
Write-Output "OK: $Jar listo. Ejecutar con:  java -jar $Jar"
Write-Output "Para construir la imagen Docker (requiere Docker Desktop corriendo):"
Write-Output "  docker build -t docker-demo ."
Write-Output "  docker run -p 8080:8080 docker-demo"
