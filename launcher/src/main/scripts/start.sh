#!/bin/bash

# Task Adapter start script checks if JRE 1.6 or newer is installed and available in path.
#
# Alexey Skorokhodov. 2012.   http://www.taskadapter.com

if [ "$(which java)" == "" ]; then
  echo "no java"
  exit 1
fi

   VER=$(java -version 2>&1 | grep 'version' | sed 's/^.*"\(.*\)".*$/\1/g')
   MAJ=$(echo $VER | awk -F. '{print $1}')
   MIN=$(echo $VER | awk -F. '{print $2}')

   FOUND_REQUIRED_JAVA=0;

if [ "${MAJ}" -gt 1 ]; then
    FOUND_REQUIRED_JAVA=1
elif [ "${MAJ}" -eq 1 ]; then
     ## "ge" means greater or equal
     if [ "${MIN}" -ge 6 ]; then
        FOUND_REQUIRED_JAVA=1
     fi
fi

if [ "$FOUND_REQUIRED_JAVA" -eq 0 ]; then
  echo "Task Adapter requires Java Runtime Environment 1.6 or newer installed and available in PATH."
  echo "Please install JRE 1.6 or newer: http://www.java.com/en/download/index.jsp"
  echo "http://www.java.com/en/download/index.jsp"
  exit 1
fi

echo "Starting TaskAdapter using Java ${VER}."
java -jar launcher.jar --port=8080  --openTaskAdapterPageInWebBrowser
