# How to contribute to the project.

## Tools you need to work on this project.
* Java 14+.
* Git.
* Some IDE like Intellij IDEA is recommended.

## Submitting a code change
Create a feature branch for each change and submit a pull request against "master" branch when ready.
All changes should have unit and integration tests, where applicable.
Please describe the change in the pull request - 

* what is being changed
* why
* how the proposed change was tested

## Logging

The project uses slf4j API with logback as the implementation mechanism. Set logging levels in `logback.xml` file
in `webui` module. Note that some older modules or libraries ignore "slf4j" and can still require log4j implementation.

## Json processing

The app uses several json libraries to generate and process json (for historical reasons). 
The recommended library to use in 2021 is Google GSon. See `JsonUtil` class.
Other libraries were added as experiments over the years. They had various problems, in particular
around collections. They should be gradually removed and replaced with Google Gson
(for consistency).

## Integration testing

Integration tests should be marked with `IntegrationTest` JUnit Category:

```
    @Category(IntegrationTest.class)
```

There is a separate Gradle command defined for these tests, to simplify local development for people who want
to exclude some tests. Feel free to extend this mechanism to exclude tests by connector (e.g. Jira, Redmine, etc)
in addition to separating by type (unit tests, integration tests). there was no need in this until now, so this has 
not been implemented. in fact, the original app idea required running integration tests for ALL connectors for every
change. this may not be applicable if you only care about one or two specific connectors.

Here is the custom Gradle command to run all integration tests:

```
    ./gradlew itest
```

`itest` is defined in the top-level "build.gradle" file.

## Error reporting
Application errors are submitted to Rollbar (if allowed by the user). See https://rollbar.com/alex2020/TaskAdapter

## How to see all Gradle dependencies

The main build file contains definition for a custom "allDeps" command. You can run it to see the dependency tree:

```
    gradle allDeps
```

## Release process.

Note: this can only be done by someone who has access to our DEV Jenkins.

* Get the latest build from Jenkins https://dev.taskadapter.com/jenkins/view/taskadapter/job/taskadapter
* With this build - verify and close issues on TaskAdapter board: https://app.asana.com/0/1199872896549300/board
* Trigger a release build that will upload the distributive to Bitbucket
  https://dev.taskadapter.com/jenkins/view/taskadapter/job/taskadapter-release/
* Make sure User Guide is up-to-date on http://www.taskadapter.com (website contents is stored in
  https://bitbucket.org/taskadapter/site git repo)
* Update and deploy website contents, including new app version tag/file, see this repo: https://bitbucket.org/taskadapter/site/overview


