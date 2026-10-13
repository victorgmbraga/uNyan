param([switch]$BuildOnly)

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$tools = Join-Path $root 'target\tools'
New-Item -ItemType Directory -Path $tools -Force | Out-Null
Add-Type -AssemblyName System.IO.Compression.FileSystem

function Get-Archive($url, $path) {
    if (-not (Test-Path $path)) {
        Write-Host "Downloading $url"
        & curl.exe --fail --location --connect-timeout 20 --max-time 600 --output "$path.part" $url
        if ($LASTEXITCODE -ne 0) { throw "Download failed: $url" }
        Move-Item -LiteralPath "$path.part" -Destination $path -Force
    }
}

$jdk = Get-ChildItem $tools -Directory -Filter 'jdk8*' | Select-Object -First 1
if (-not $jdk) {
    $zip = Join-Path $tools 'jdk8.zip'
    Get-Archive 'https://api.adoptium.net/v3/binary/latest/8/ga/windows/x64/jdk/hotspot/normal/eclipse' $zip
    [System.IO.Compression.ZipFile]::ExtractToDirectory($zip, $tools)
    $jdk = Get-ChildItem $tools -Directory -Filter 'jdk8*' | Select-Object -First 1
}
if (-not $jdk -or -not (Test-Path (Join-Path $jdk.FullName 'bin\javac.exe'))) {
    throw 'The portable Java 8 JDK was not found.'
}

$maven = Join-Path $tools 'apache-maven-3.9.9\bin\mvn.cmd'
if (-not (Test-Path $maven)) {
    $zip = Join-Path $tools 'maven.zip'
    Get-Archive 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' $zip
    [System.IO.Compression.ZipFile]::ExtractToDirectory($zip, $tools)
}

$repository = Join-Path $tools 'legacy\ubiquitos\trunk\src\Java\maven'
if (-not (Test-Path (Join-Path $repository 'org\unbiquitous\uImpala-jse\1.0\uImpala-jse-1.0.jar'))) {
    $zip = Join-Path $tools 'source-archive.zip'
    Get-Archive 'https://storage.googleapis.com/google-code-archive-source/v2/code.google.com/ubiquitos/source-archive.zip' $zip
    $archive = [System.IO.Compression.ZipFile]::OpenRead($zip)
    try {
        # Recover only the Maven repository, not the archived sources or SVN metadata.
        $prefix = 'ubiquitos/trunk/src/Java/maven/'
        foreach ($entry in $archive.Entries) {
            if (-not $entry.FullName.StartsWith($prefix) -or $entry.FullName.Contains('/.svn/') -or $entry.FullName.EndsWith('/')) { continue }
            $relative = $entry.FullName.Substring($prefix.Length)
            if ($relative.Split('/') -contains '..') { throw 'Unsafe archive path.' }
            $destination = Join-Path $repository $relative
            New-Item -ItemType Directory -Path (Split-Path $destination) -Force | Out-Null
            [System.IO.Compression.ZipFileExtensions]::ExtractToFile($entry, $destination, $true)
        }
    } finally {
        $archive.Dispose()
    }
}

$settings = Join-Path $tools 'legacy-settings.xml'
$repositoryUrl = [System.Security.SecurityElement]::Escape(([uri]($repository + '\')).AbsoluteUri)
@"
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <mirrors>
    <mirror>
      <id>ubiquitos-archive-local</id>
      <mirrorOf>ubiquitos</mirrorOf>
      <url>$repositoryUrl</url>
    </mirror>
  </mirrors>
</settings>
"@ | Set-Content -LiteralPath $settings -Encoding UTF8

$previousJavaHome = $env:JAVA_HOME
Push-Location $root
try {
    $env:JAVA_HOME = $jdk.FullName
    & $maven --batch-mode --no-transfer-progress -s $settings "-Dmaven.repo.local=$tools\m2" '-Dmaven.compiler.source=1.8' '-Dmaven.compiler.target=1.8' compile com.googlecode.mavennatives:maven-nativedependencies-plugin:0.0.7:copy org.apache.maven.plugins:maven-dependency-plugin:3.8.1:copy-dependencies
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed. See the errors above.' }
    if (-not $BuildOnly) {
        & (Join-Path $jdk.FullName 'bin\java.exe') '-Djava.library.path=target/natives' -cp 'target/classes;target/dependency/*' com.victorbraga.unyan.Main
        if ($LASTEXITCODE -ne 0) { throw 'The game exited with an error. See the output above.' }
    }
} finally {
    $env:JAVA_HOME = $previousJavaHome
    Pop-Location
}
