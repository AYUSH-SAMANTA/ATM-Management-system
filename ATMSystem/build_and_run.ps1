# PowerShell Build & Run Script for ATM System
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "       Compiling and Starting Java ATM System...        " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src" -Filter *.java -Recurse | Select-Object -ExpandProperty FullName

Write-Host "Compiling $($sources.Count) Java source files..." -ForegroundColor Yellow
javac -d "bin" $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful! Launching ATM..." -ForegroundColor Green
    Write-Host ""
    java -cp "bin" com.atm.Main
} else {
    Write-Host "[ERROR] Compilation failed. Please ensure JDK is installed and in your PATH." -ForegroundColor Red
}
