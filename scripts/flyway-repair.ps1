# Flyway Repair Script
# This repairs the Flyway schema history when migration checksums change

Write-Host "Running Flyway Repair..." -ForegroundColor Cyan

# Load environment variables from .env
if (Test-Path ".\.env") {
    Get-Content ".\.env" | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]*)\s*=\s*(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
    Write-Host "Loaded .env file" -ForegroundColor Green
} else {
    Write-Host ".env file not found" -ForegroundColor Yellow
}

# Run Maven Flyway repair
mvn flyway:repair

Write-Host ""
Write-Host "Flyway repair completed!" -ForegroundColor Green
Write-Host "You can now run the application normally." -ForegroundColor Cyan



