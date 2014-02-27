cd C:\GUIDE8\
@echo off
if not exist scope.mar goto err_msg
copy STARTUP.MAR startup.old
del STARTUP.MAR
copy SCOPE.MAR STARTUP.MAR
goto start_guide
:err_msg
echo WARNING: Scope.mar wasn't found!
echo Project Pluto Guide will not start at the correct coordinates.
pause
:start_guide
guide8.exe
