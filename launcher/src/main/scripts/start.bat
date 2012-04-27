@echo off

REM Task Adapter start script checks if JRE 1.6 or newer is installed and available in path.
REM
REM  Alexey Skorokhodov. 2012.   http://www.taskadapter.com

setlocal

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
@Rem     @echo Output: %%g
    set JAVAVER=%%g
)

if "%JAVAVER%" == "" (
  echo Task Adapter requires Java Runtime Environment 1.6 installed and available in PATH. Please install JRE 1.6 or newer.
  echo No Java was found in PATH.
  echo http://www.java.com/en/download/index.jsp
  goto:EOF
)

set JAVAVER=%JAVAVER:"=%

set Major=0
set Minor=0

for /f "delims=. tokens=1-3" %%v in ("%JAVAVER%") do (
    set Major=%%v
    set Minor=%%w
@Rem     @echo Major: %%v
@Rem     @echo Minor: %%w
@Rem    @echo Build: %%x
)

@REM if Major GEQ 1
if %Major% geq 1 if %Minor% geq 6 (
  echo Starting Task Adapter using Java %JAVAVER%
  java -jar launcher.jar --port=8080 --openTaskAdapterPageInWebBrowser
) else (
  echo Task Adapter requires Java Runtime Environment 1.6 or newer installed and available in PATH.
  echo Java found on your machine is: %JAVAVER%
  echo Please install JRE 1.6 or newer: http://www.java.com/en/download/index.jsp
)

endlocal
