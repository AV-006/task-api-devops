$ErrorActionPreference = 'Stop'
mvn -B clean test
mvn -B package -DskipTests
Write-Host 'Build complete: target\task-api.jar'
