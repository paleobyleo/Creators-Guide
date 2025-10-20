# PowerShell script to build the release APK
Write-Host "Building release APK..." -ForegroundColor Green

# Set environment variables
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17\jdk-17.0.16+8"
$env:GRADLE_OPTS = '-Dorg.gradle.java.home="C:\Program Files\Java\jdk-17\jdk-17.0.16+8"'

Write-Host "JAVA_HOME is set to: $env:JAVA_HOME" -ForegroundColor Yellow

# Run the gradle build
.\gradlew.bat assembleRelease

if ($LASTEXITCODE -eq 0) {
    Write-Host "Release build completed successfully!" -ForegroundColor Green
    Write-Host "APK should be located at: app\build\outputs\apk\release\" -ForegroundColor Yellow
} else {
    Write-Host "Build failed with error code: $LASTEXITCODE" -ForegroundColor Red
}

Write-Host "Press any key to continue..."
$host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")