# Flyway Migration Script for Local Development
# Chạy script này để migrate database THAY VÌ để Spring Boot tự động migrate

Write-Host "🚀 Running Flyway Migration..." -ForegroundColor Cyan

# Load .env file
if (Test-Path .env) {
    Write-Host "✅ Loading .env file..." -ForegroundColor Green
    Get-Content .env | ForEach-Object {
        if ($_ -match '^([^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
} else {
    Write-Host "⚠️  .env file not found. Using default DATABASE_URL." -ForegroundColor Yellow
}

# Run Flyway migrate
Write-Host "🔄 Executing: mvn flyway:migrate" -ForegroundColor Cyan
mvn flyway:migrate

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Migration completed successfully!" -ForegroundColor Green
} else {
    Write-Host "❌ Migration failed with exit code: $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

