@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0demos\build-demo.ps1" -Day 5 -Run
if errorlevel 1 pause
