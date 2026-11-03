param([ValidateRange(2,5)][int]$Day = 2)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$output = Join-Path $projectRoot ('build/test-day' + $Day + '/' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force $output | Out-Null
$sources = @(Get-ChildItem (Join-Path $PSScriptRoot "day$Day/src") -Filter *.java -Recurse | ForEach-Object { $_.FullName })
$tests = @(Get-ChildItem (Join-Path $PSScriptRoot "day$Day/test") -Filter *.java -Recurse | ForEach-Object { $_.FullName })
& javac --release 21 -encoding UTF-8 -d $output $sources $tests
if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
& java -cp $output demo.AppointmentTest
if ($LASTEXITCODE -ne 0) { throw 'Appointment tests failed.' }
