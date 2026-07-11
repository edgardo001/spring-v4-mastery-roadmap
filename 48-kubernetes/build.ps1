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

$Jar = Join-Path $PSScriptRoot 'target\kubernetes-1.0.0.jar'
if (-not (Test-Path $Jar)) { throw "ERROR: $Jar no existe" }
Write-Output "OK: $Jar listo. Ejecutar con:  java -jar $Jar"
Write-Output ""
Write-Output "Siguientes pasos (fuera de este script):"
Write-Output "  docker build -t kubernetes-demo:1.0.0 ."
Write-Output "  kubectl apply -f k8s/"
Write-Output "  # o alternativamente:  helm install kubernetes-demo ./helm"
