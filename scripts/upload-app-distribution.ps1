<#
.SYNOPSIS
    Sube el APK release de EjeSpot a Firebase App Distribution.

.DESCRIPTION
    Usa Firebase CLI (firebase-tools). Si no está instalada, la instala con npm.
    El token se obtiene con: firebase login:ci

.EXAMPLE
    .\scripts\upload-app-distribution.ps1 -AppId "1:413793524043:android:xxxxxxxx" -Token "1//0gxxxx" -ReleaseNotes "Entrega final - correcciones de reseñas y propuestas"
#>
param(
    [Parameter(Mandatory = $true)] [string] $AppId,
    [string] $Token = "",
    [string] $ApkPath = "app/build/outputs/apk/release/app-release.apk",
    [string] $ReleaseNotes = "Entrega final: mapas, persistencia, IA, i18n, imagenes en la nube y recuperacion de contrasena",
    [string] $Groups = "testers"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$apk = Join-Path $root $ApkPath

if (-not (Test-Path $apk)) {
    throw "No existe el APK: $apk. Ejecuta antes: .\gradlew assembleRelease"
}

if (-not (Get-Command firebase -ErrorAction SilentlyContinue)) {
    Write-Host "Instalando firebase-tools..."
    npm install -g firebase-tools
}

Write-Host "Subiendo $apk a App Distribution (App ID: $AppId)..."
$args = @("appdistribution:distribute", $apk, "--app", $AppId, "--groups", $Groups, "--release-notes", $ReleaseNotes)
if (-not [string]::IsNullOrWhiteSpace($Token)) {
    $args += @("--token", $Token)
}
& "$env:APPDATA\npm\firebase.cmd" @args

if ($LASTEXITCODE -ne 0) {
    throw "La subida fallo con codigo $LASTEXITCODE"
}

Write-Host "APK distribuido correctamente a los testers del grupo '$Groups'."
