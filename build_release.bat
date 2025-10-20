@echo off
setlocal

REM Set JAVA_HOME to the correct JDK path (properly quoted)
set "JAVA_HOME=C:\Program Files\Java\jdk-17\jdk-17.0.16+8"
set "GRADLE_OPTS=-Dorg.gradle.java.home=\"C:\Program Files\Java\jdk-17\jdk-17.0.16+8\""

echo Building release APK...
echo JAVA_HOME is set to: %JAVA_HOME%

REM Run the gradle build
call gradlew.bat assembleRelease

if %ERRORLEVEL% EQU 0 (
    echo Release build completed successfully!
    echo APK should be located at: app\build\outputs\apk\release\
) else (
    echo Build failed with error code: %ERRORLEVEL%
)

pause