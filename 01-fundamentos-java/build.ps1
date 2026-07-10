$ErrorActionPreference = 'Stop'
$Root = Resolve-Path (Join-Path $PSScriptRoot '..')
$Jdk  = Join-Path $Root 'jdk-21.0.11+10\bin'
$Name = 'fundamentos-java-1.0.0'

Set-Location $PSScriptRoot
if (Test-Path out)    { Remove-Item -Recurse -Force out }
if (Test-Path target) { Remove-Item -Recurse -Force target }
New-Item -ItemType Directory -Path out, target | Out-Null

$sources = @(
    'src/records/ClienteDto.java',
    'src/streams/ProcesadorStreams.java',
    'src/optional/UsuarioServiceMock.java',
    'src/Main.java'
)

& "$Jdk\javac.exe" -d out $sources
& "$Jdk\jar.exe"   --create --file "target/$Name.jar" --main-class=Main -C out .

Write-Host "== Running tests =="
& "$Jdk\java.exe" -jar "target/$Name.jar"

Write-Host "== Artifact =="
Get-Item "target/$Name.jar"
