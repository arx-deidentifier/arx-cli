#!/bin/sh

if [ $# -eq 0 ]; then
    exec "@@INSTALLDIR@@/arx-launcher.run"
    exit 0
fi

exec "@@JAVAEXECUTABLE@@" -jar "@@INSTALLDIR@@/arx-@@VERSION@@-cli.jar" $*