@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0demos\build-demo.ps1" -Day 1 -Run
if errorlevel 1 pause
