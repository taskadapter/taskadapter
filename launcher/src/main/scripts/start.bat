start java -jar launcher.jar --port=8080

@echo ------------------------------------------------
@echo Waiting 2 seconds for Task Adapter application to start...
@echo ------------------------------------------------
sleep 2

@REM open the default web browser
start http://localhost:8080/ta
