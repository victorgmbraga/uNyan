# Serve the generated web build over HTTP.
# Usage: powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\serve.ps1 [-Port 8080] [-BuildDir .\web\target\unyan-web]
param(
    [int]$Port = 8080,
    [string]$BuildDir = (Join-Path $PSScriptRoot 'target\unyan-web')
)

$ErrorActionPreference = 'Stop'

$listener = [System.Net.HttpListener]::new()
$listener.Prefixes.Add("http://localhost:$Port/")
try {
    $listener.Start()
} catch {
    throw ("Could not bind http://localhost:{0}/ (port in use? Run as another user?)" -f $Port)
}

$map = [System.Collections.Generic.Dictionary[string, string]]::new()
$map['.html'] = 'text/html; charset=utf-8'
$map['.js'] = 'text/javascript; charset=utf-8'
$map['.wasm'] = 'application/wasm'
$map['.txt'] = 'text/plain; charset=utf-8'
$map['.png'] = 'image/png'
$map['.ogg'] = 'audio/ogg'
$map['.map'] = 'application/json'
$map['.teadbg'] = 'application/json'

Write-Host "Serving $BuildDir at http://localhost:$Port/ (Ctrl+C to stop)"
try {
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $response = $context.Response
        try {
            $relative = [Uri]::UnescapeDataString(($context.Request.Url -as [uri]).AbsolutePath)
            if ($relative -eq '/') { $relative = '/index.html' }

            $full = [System.IO.Path]::GetFullPath((Join-Path $BuildDir $relative.TrimStart('/')))
            if (-not ($full.StartsWith([System.IO.Path]::GetFullPath((Resolve-Path $BuildDir).Path)))) {
                $response.StatusCode = 403
            } elseif (-not (Test-Path -LiteralPath $full -PathType Leaf)) {
                $response.StatusCode = 404
            } else {
                $extension = [System.IO.Path]::GetExtension($full).ToLowerInvariant()
                if ($map.ContainsKey($extension)) {
                    $response.ContentType = $map[$extension]
                } else {
                    $response.ContentType = 'application/octet-stream'
                }
                $buffer = [System.IO.File]::ReadAllBytes($full)
                $response.ContentLength64 = $buffer.Length
                $response.OutputStream.Write($buffer, 0, $buffer.Length)
            }
        } catch {
            try { $response.StatusCode = 500 } catch { }
        } finally {
            try { $response.OutputStream.Close() } catch { }
        }
    }
} finally {
    try { $listener.Stop() } catch { }
}
