# Load .env file and export variables to the current process, then run the Spring Boot app
# Usage: ./scripts/run-local.ps1

$envFile = Join-Path $PSScriptRoot "..\.env" | Resolve-Path -ErrorAction SilentlyContinue
if (-not $envFile) {
    Write-Error ".env file not found in project root. Copy .env.example to .env and edit as needed."
    exit 1
}

# Read .env lines
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) { return }
    $kv = $line -split "=",2
    if ($kv.Length -eq 2) {
        $key = $kv[0].Trim()
        $value = $kv[1].Trim()
        # Unescape surrounding quotes if present
        if ($value.StartsWith('"') -and $value.EndsWith('"')) { $value = $value.Trim('"') }
        if ($value.StartsWith("'") -and $value.EndsWith("'")) { $value = $value.Trim("'") }
        Write-Host "Setting env: $key"
        $env:$key = $value
    }
}

# Run the app
mvn spring-boot:run

