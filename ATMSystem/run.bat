@echo off
title ATM System - Global Apex Bank
echo ========================================================
echo       Compiling and Starting Java ATM System...
echo ========================================================

if not exist "bin" mkdir bin

echo Compiling Java source files...
javac -d bin src\com\atm\model\*.java src\com\atm\interfaces\*.java src\com\atm\service\*.java src\com\atm\repository\*.java src\com\atm\ui\*.java src\com\atm\Main.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful! Launching ATM...
    echo.
    java -cp bin com.atm.Main
) else (
    echo.
    echo [ERROR] Compilation failed. Please ensure JDK is installed and in your PATH.
    pause
)
