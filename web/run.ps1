param([switch]$BuildOnly)

# Builds the wasm port and serves it in a browser.
#   powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\web\run.ps1
#   powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\web\run.ps1 -BuildOnly
$ErrorActionPreference = 'Stop'

$root = Split-Path $PSScriptRoot -Parent
$tools = Join-Path $root 'target\tools'
$build = Join-Path $PSScriptRoot 'target\unyan-web'
Add-Type -AssemblyName System.IO.Compression.FileSystem

function Get-Archive($url, $path) {
    if (-not (Test-Path $path)) {
        Write-Host "Downloading $url"
        & curl.exe --fail --location --connect-timeout 20 --max-time 600 --output "$path.part" $url
        if ($LASTEXITCODE -ne 0) { throw "Download failed: $url" }
        Move-Item -LiteralPath "$path.part" -Destination $path -Force
    }
}

# TeaVM 0.16 requires a modern JDK; the demo game keeps the Java 8 desktop build untouched.
$jdk = Get-ChildItem $tools -Directory -Filter 'jdk-17*' | Select-Object -First 1
if (-not $jdk) {
    $zip = Join-Path $tools 'jdk17.zip'
    Get-Archive 'https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse' $zip
    [System.IO.Compression.ZipFile]::ExtractToDirectory($zip, $tools)
    $jdk = Get-ChildItem $tools -Directory -Filter 'jdk-17*' | Select-Object -First 1
}
if (-not $jdk -or -not (Test-Path (Join-Path $jdk.FullName 'bin\javac.exe'))) {
    throw 'The portable Java 17 JDK was not found.'
}

$maven = Join-Path $tools 'apache-maven-3.9.9\bin\mvn.cmd'
if (-not (Test-Path $maven)) {
    $zip = Join-Path $tools 'maven.zip'
    Get-Archive 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' $zip
    [System.IO.Compression.ZipFile]::ExtractToDirectory($zip, $tools)
}

$previousJavaHome = $env:JAVA_HOME
Push-Location $root
try {
    $env:JAVA_HOME = $jdk.FullName
    & $maven --batch-mode --no-transfer-progress -f web\pom.xml '-Dmaven.repo.local=target\tools\m2' package
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed. See the errors above.' }
} finally {
    $env:JAVA_HOME = $previousJavaHome
    Pop-Location
}

# Assemble the playable directory: host page, wasm module + TeaVM runtime
# (already produced by Maven in $build), the game assets and the asset
# manifest. Only our own pieces are removed, never the Maven wasm output.
$imgDir = Join-Path $build 'img'
$sfxDir = Join-Path $build 'sfx'
$manifest = Join-Path $build 'asset-manifest.txt'
New-Item -ItemType Directory -Path $build -Force | Out-Null
foreach ($path in $imgDir, $sfxDir, $manifest) {
    if (Test-Path $path) { Remove-Item -Recurse -Force $path }
}
Copy-Item (Join-Path $PSScriptRoot 'index.html') $build -Force
New-Item -ItemType Directory -Path $imgDir -Force | Out-Null
New-Item -ItemType Directory -Path $sfxDir -Force | Out-Null

$assets = ''
foreach ($file in Get-ChildItem (Join-Path $root 'img') -File) {
    # screenshots (demo/smoke images) are documentation, not engine assets
    if ($file.Name -like '*-demo*' -or $file.Name -like '*-smoke*') { continue }
    Copy-Item $file.FullName (Join-Path $imgDir $file.Name) -Force
    $assets += "img img/$($file.Name)`n"
}
foreach ($file in Get-ChildItem (Join-Path $root 'sfx') -File) {
    Copy-Item $file.FullName (Join-Path $sfxDir $file.Name) -Force
    $assets += "audio sfx/$($file.Name)`n"
}
[System.IO.File]::WriteAllText($manifest, $assets, [System.Text.Encoding]::UTF8)

if ($BuildOnly) {
    Write-Host ("Web build assembled at {0}" -f $build)
    return
}

# Serve and open the demo.
$server = Start-Process -FilePath 'powershell.exe' -ArgumentList @(
    '-NoProfile', '-ExecutionPolicy', 'Bypass', '-File',
    (Join-Path $PSScriptRoot 'serve.ps1'), '-Port', '8080', '-BuildDir', $build
) -PassThru
Start-Sleep -Seconds 1
Start-Process 'http://localhost:8080/'
Write-Host 'Serving http://localhost:8080/ - close this window (or press Ctrl+C) to stop the server.'
try {
    Wait-Process -Id $server.Id
} catch {
}
