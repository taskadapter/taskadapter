# TaskAdapter


## Tools you need to work on this project.
* Java 14. 
* Some IDE like Eclipse 3.7 or IDEA is recommended. See TA_IDE_guide_eclipse and TA_IDE_guide_IDEA documents in GDrive for Eclipse/IDEA-specific instructions.
* Gradle build tool. Gradle will be downloaded automatically by Gradle Wrapper. See the current Gradle version
  used by this project in `gradle/gradle.properties` file).
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

##  Run in IDE

1. Import the project into your IDE
2. Run `./gradlew vaadinPrepareFrontend` in the project, to configure Vaadin for npm mode.
3. Create a run configuration, optionally add this parameter to the program arguments list:

* `--openTaskAdapterPageInWebBrowser` - open a web browser after the server start

3. Run/Debug the `TADevLauncher` class as an application (run the `main()` method).
   The app will use `npm` to download all javascript libraries (will take a long time)
   and will start in development mode.

## How to build from source code

    ./gradlew clean build -Pvaadin.productionMode
    
This creates a ZIP file in taskadapter\webui\build\distributions folder. Unpack the ZIP anywhere,
run `bin/webui` script. It will automatically open TA URL in browser when the web app is ready.

## How to run locally

Pre-requisites
* have Jetty 9.4.9 or newer, to get Java9 modules support (must have)
* disable Vaadin Gradle usage reporting to avoid a failing NPM "integrity" check:


    /projects/taskadapter/webui$ npm explore @vaadin/vaadin-usage-statistics -- npm run disable
* Register the Jetty instance in your IDEA, add the WAR artifact (will be created during the build)
as deployment.
* Start the Jetty server from IDEA (run configuration). 

# How to see all Gradle dependencies

The main build file contains definition for a custom "allDeps" command. You can run it to see the dependency tree: 

```
    gradle allDeps
```
   
## Our dev infrastructure.
Dev servers, Jenkins, etc are described here (including access passwords):
https://docs.google.com/spreadsheets/d/1UsV1G1iCY-e2Qo7V6Y7_zWdFrtB6-IquQuGSA-1NGy0/edit#gid=0&vpid=A2

## Release process.
* Get the latest build from Jenkins https://dev.taskadapter.com/jenkins/view/taskadapter/job/taskadapter
* With this build - verify and close issues on TaskAdapter board: https://app.asana.com/0/1199872896549300/board
* Trigger a release build that will upload the distributive to Bitbucket 
https://dev.taskadapter.com/jenkins/view/taskadapter/job/taskadapter-release/
* Make sure User Guide is up-to-date on http://www.taskadapter.com (website contents is stored in 
https://bitbucket.org/taskadapter/site git repo)
* Update and deploy website contents, including new app version tag/file, see this repo: https://bitbucket.org/taskadapter/site/overview

## Logging

The project uses slf4j API with logback as the implementation mechanism. Set logging levels in `logback.xml` file
in `webui` module. Note that some older modules or libraries ignore ""slf4j" and can still require log4j implementation.

## Error reporting

Application errors are submitted to Rollbar. See https://rollbar.com/alex2020/TaskAdapter
