param([ValidateRange(1,5)][int]$Day = 1, [switch]$Run)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$sourceRoot = Join-Path $PSScriptRoot "day$Day/src"
if (!(Test-Path $sourceRoot)) { throw "Day $Day has not been prepared yet." }
# A fresh output directory ensures old classes cannot hide missing dependencies.
$buildRoot = Join-Path $projectRoot ("build/demo-day$Day/" + [guid]::NewGuid().ToString('N'))
$classes = Join-Path $buildRoot 'classes'
$temp = Join-Path $buildRoot 'tmp'
$dist = Join-Path $projectRoot "dist/demos/day$Day"
New-Item -ItemType Directory -Force $classes, $temp, $dist | Out-Null
$sources = @(Get-ChildItem $sourceRoot -Recurse -Filter *.java | ForEach-Object { $_.FullName })
& javac --release 21 -encoding UTF-8 -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw "Day $Day compilation failed." }
# Windows locks a JAR while Java is running it. Never overwrite a running build.
$jarPath = Join-Path $dist ('SchedulerDemo-' + [guid]::NewGuid().ToString('N') + '.jar')
& jar "-J-Djava.io.tmpdir=$temp" --create --file $jarPath --main-class demo.Main -C $classes .
if ($LASTEXITCODE -ne 0) { throw "Day $Day packaging failed." }
Write-Host "Day $Day built successfully."
$javaw = (Get-Command javaw -ErrorAction Stop).Source
$shortcutPath = Join-Path $projectRoot "Day $Day Demo.lnk"
$shell = New-Object -ComObject WScript.Shell
$shortcut = $shell.CreateShortcut($shortcutPath)
$shortcut.TargetPath = $javaw
$shortcut.Arguments = '-jar "' + $jarPath + '"'
$shortcut.WorkingDirectory = $dist
$shortcut.Description = "Open the Day $Day scheduler demonstration"
$shortcut.Save()
Write-Host "Open Day $Day Demo (the shortcut) to run without a terminal."
if ($Run) {
    # javaw opens only the GUI. The data directory is isolated from the full app.
    Start-Process -FilePath $javaw -ArgumentList $shortcut.Arguments -WorkingDirectory $dist -WindowStyle Hidden
}
