@echo off

set newline=^&echo.

FIND /C /I "user-service" %WINDIR%\system32\drivers\etc\hosts
IF %ERRORLEVEL% NEQ 0 ECHO %newline%127.0.0.1 user-service>>%WINDIR%\System32\drivers\etc\hosts

FIND /C /I "auth-service" %WINDIR%\system32\drivers\etc\hosts
IF %ERRORLEVEL% NEQ 0 ECHO %newline%127.0.0.1 auth-service>>%WINDIR%\System32\drivers\etc\hosts

FIND /C /I "order-service" %WINDIR%\system32\drivers\etc\hosts
IF %ERRORLEVEL% NEQ 0 ECHO %newline%127.0.0.1 order-service>>%WINDIR%\System32\drivers\etc\hosts

FIND /C /I "gateway-service" %WINDIR%\system32\drivers\etc\hosts
IF %ERRORLEVEL% NEQ 0 ECHO %newline%127.0.0.1 gateway-service>>%WINDIR%\System32\drivers\etc\hosts