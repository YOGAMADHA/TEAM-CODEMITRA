@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Startup Batch Script for Windows
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%" == "" @ECHO OFF
@SETLOCAL

where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    mvn %*
    exit /b %ERRORLEVEL%
)

echo Maven executable not found in PATH. Please install Apache Maven 3.9+ or Java 21 JDK.
exit /b 1
