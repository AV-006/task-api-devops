$ErrorActionPreference = 'Stop'
if (-not (Test-Path 'target/task-api.jar')) { throw 'Build first with mvn clean package' }
$env:APP_PORT = '8080'
$env:APP_ENV = 'local'
java -jar target/task-api.jar
