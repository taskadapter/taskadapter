# TaskAdapter


## Tools you need to work on this project.
* Some IDE like Eclipse 3.7 or IDEA. See TA_IDE_guide_eclipse and TA_IDE_guide_IDEA documents in GDrive for Eclipse/IDEA-specific instructions.
* Gradle.
* Git.

## Source code locations.

### Main application (TaskAdapter)
Our source code is located in a Git repository on Bitbucket.com website. Everyone needs to:
* register an individual account there
* create private & public keys. the file with private key must be called “id_rsa” and must be in your <user_home>/.ssh folder.
* upload the public key (“id_rsa.pub” file) to the server.

See http://help.github.com/win-set-up-git/ for a tutorial on how to use Git.

Clone this repo: https://bitbucket.org/taskadapter/taskadapter

Create a feature branch for each change and submit a pull request against "master" branch when ready.

### Old Eclipse-based (desktop app) Task Adapter code
It is located in a separate GIT repository: https://bitbucket.org/taskadapter/eclipse
It was used for TA versions 1.xx. Not supported anymore.

### License Generator web app
This web app is deployed to a googleapp server. It is used to manually generate licenses.
https://bitbucket.org/taskadapter/license-generator

### License module
This module checks licenses. Used by both License Generator web app and TaskAdapter app.
https://bitbucket.org/taskadapter/license

### Redmine Java API source code.

Task Adapter’s Redmine Connector uses Redmine Java API. The source code is here: https://github.com/taskadapter/redmine-java-api

## How to build from source code

Compile and test:

    gradle clean build

Full build:

    gradle clean build distZip
This creates a ZIP file in taskadapter\launcher\build\distributions folder. Unpack the ZIP anywhere,
run bin/launch.bat. It will automatically open TA URL in browser when the web app is ready.

## Our dev infrastructure.
Dev servers, Jenkins, etc are described here (including access passwords):
https://docs.google.com/spreadsheets/d/1UsV1G1iCY-e2Qo7V6Y7_zWdFrtB6-IquQuGSA-1NGy0/edit#gid=0&vpid=A2

## Release process.
* Make sure you have the latest code from Git server.
* Change `version` in `/gradle.properties`
* Push everything to Git.
* Get the latest build from Jenkins https://dev.taskadapter.com/jenkins/view/taskadapter/job/taskadapter
* Verify and close issues on TaskAdapter Trello board: https://trello.com/b/eLGOvlZa/ta
* Tag the current revision in Git (e.g. "release-3.8.0").
* Make sure User Guide is up-to-date on http://www.taskadapter.com (website contents is stored in 
https://bitbucket.org/taskadapter/site git repo)
* Trigger a release build that will upload the distributive to Bitbucket 
https://dev.taskadapter.com/jenkins/view/taskadapter/job/taskadapter-release/

* Follow release steps in "site" repo: https://bitbucket.org/taskadapter/site/overview

## Logging

Logging is done via slf4j + log4j. Set logging levels in `log4j.properties` file in `webui` module.

