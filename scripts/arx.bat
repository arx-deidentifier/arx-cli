@ECHO OFF

if [%1]==[] GOTO GUI
if [%1]==[-?] GOTO CLI
if [%1]==[--help] GOTO CLI
if [%2]==[] GOTO GUI

:CLI
"@@JAVAEXECUTABLE@@" -jar "@@INSTALLDIR@@\arx-@@VERSION@@-cli.jar" %*
GOTO EOF

:GUI
"@@INSTALLDIR@@\arx-launcher.exe"

:EOF