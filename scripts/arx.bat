@ECHO OFF

if [%1]==[] GOTO GUI

"@@JAVAEXECUTABLE@@" -jar "@@INSTALLDIR@@\arx-@@VERSION@@-cli.jar" %*
GOTO EOF

:GUI
"@@INSTALLDIR@@\arx-launcher.exe"

:EOF