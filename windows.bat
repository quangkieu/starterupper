rem create registry file to add the protocol
IF "%1"=="" GOTO default else GOTO config
:config
rem if [NOT] "%7"=="" GOTO end
start /d %~dp0 web2.sh web2.sh %1 %2 %3 %4 %5 %6
goto:end
:default
echo Windows Registry Editor Version 5.00 > "temp.reg"
echo. >> "temp.reg"
echo [HKEY_CLASSES_ROOT\gitconfig] >> "temp.reg"
echo @="URL:gitconfig Protocol" >> "temp.reg"
echo "URL Protocol"="" >> "temp.reg"
echo [HKEY_CLASSES_ROOT\gitconfig\shell] >> "temp.reg"
echo [HKEY_CLASSES_ROOT\gitconfig\shell\open] >> "temp.reg"
echo [HKEY_CLASSES_ROOT\gitconfig\shell\open\command] >> "temp.reg"
set _=%~dp0
set _=%_:"=%
set _=%_:\=\\%
echo @="\"%_%windows.bat\" %%1 %%2 %%3 %%4 %%5 %%6" >> "temp.reg"
rem run the reg file
regedit.exe /S "%~dp0temp.reg"
rem run normal web2.sh
start /d "%~dp0" "web2.sh" "web2.sh"
:end
exit