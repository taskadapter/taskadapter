# TaskAdapter
TaskAdapter is an application to transfer tasks between various bug tracking and project management systems.
E.g. between Atlassian Jira, Redmine, Microsoft Project, etc.
It is implemented in Java language. It has a web UI module to support editing synchronization configs and start export 
process.

## Pre-requisites to run the application locally

* Have Java development Kit (JDK) version 14+ installed.
* have npm installed (`sudo apt install npm`)
* run the following command to disable Vaadin Gradle usage reporting (to avoid a failing NPM "integrity" check):
```
    npm explore @vaadin/vaadin-usage-statistics -- npm run disable
```

##  Run the application in an IDE

1. Run `./gradlew vaadinPrepareFrontend` command in the project folder, to configure Vaadin for npm mode.
2. Import the project into your IDE (say, Intellij IDEA). You can open "build.gradle" file in the project root folder.
3. Run/Debug the `TADevLauncher` class as an application (run the `main()` method).
   The app will use `npm` to download all javascript libraries (will take a long time)
   and will start in development mode.
4. Optionally, add the following parameter to `TADevLauncher` run configuration arguments list:

* `--openTaskAdapterPageInWebBrowser` 

this will open a web browser on app start.

## Build and run from command line

Run this in the project root folder:
```
    ./gradlew clean build -Pvaadin.productionMode -ProllbarApiTokenProperty=<token_here>
```    

This will create a ZIP file in `webui/build/distributions` folder. Unpack the ZIP anywhere,
run `bin/taskadapter` script. It will automatically open TA URL in browser when the web app is ready.

