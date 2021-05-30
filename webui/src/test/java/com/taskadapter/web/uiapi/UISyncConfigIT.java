package com.taskadapter.web.uiapi;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import com.taskadapter.test.core.IntegrationTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class UISyncConfigIT {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void beforeEachTest() {
        ConfigFolderTestConfigurer.configure(tempFolder.getRoot());
    }

    // TODO this test requires some "Epic" tasks to be present in JIRA. it should create them itself
    @Test
    public void tasksLoadedFromJiraAndSavedToRedmine() throws IOException, ConnectorException {
        var config = ConfigLoader.loadConfig(tempFolder.getRoot(), "Atlassian-JIRA_Redmine.conf");
        var loadedTasks = UISyncConfig.loadTasks(config, 100);
        assertThat(loadedTasks.size()).isGreaterThan(0);
        var saveResult = config.saveTasks(loadedTasks, ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(saveResult.hasErrors()).isFalse();
        assertThat(saveResult.getCreatedTasksNumber()).isEqualTo(loadedTasks.size());
    }

    @Test
    public void emptyDescriptionFieldNameOnRightSideIsIgnoredIfSelectedIsFalse() throws IOException, ConnectorException {
        var config = ConfigLoader.loadConfig(tempFolder.getRoot(), "JIRA_Redmine_empty_description_on_right_side.conf");
        var loadedTasks = UISyncConfig.loadTasks(config, 100);
        assertThat(loadedTasks.size()).isGreaterThan(0);
        var saveResult = config.saveTasks(loadedTasks, ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(saveResult.hasErrors()).isFalse();
        assertThat(saveResult.getCreatedTasksNumber()).isEqualTo(loadedTasks.size());
    }

    @Test
    public void fakeJiraTaskIsCreatedThenUpdatedInRedmine() throws IOException, ConnectorException {
        var config = ConfigLoader.loadConfig(tempFolder.getRoot(), "Atlassian-JIRA_Redmine.conf");
        var jiraTask = new GTask();
        jiraTask.setId(66l);
        jiraTask.setKey("TEST-66");
        jiraTask.setValue(AllFields.summary, "summary");

        var list = List.of(jiraTask);
        var saveResult = config.saveTasks(list, ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(saveResult.hasErrors()).isFalse();
        assertThat(saveResult.getCreatedTasksNumber()).isEqualTo(1);
        assertThat(saveResult.getUpdatedTasksNumber()).isEqualTo(0);

        // now pretend that the task was loaded from somewhere
        jiraTask.setSourceSystemId(new TaskId(66l, "TEST-66"));
        var updateResult = config.saveTasks(list, ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(updateResult.hasErrors()).isFalse();
        assertThat(updateResult.getCreatedTasksNumber()).isEqualTo(0);
        assertThat(updateResult.getUpdatedTasksNumber()).isEqualTo(1);
    }

    @Test
    public void taskWithRemoteIdIsUpdatedInMantisBT() throws IOException, ConnectorException {
        var config = ConfigLoader.loadConfig(tempFolder.getRoot(), "Mantis_1-Microsoft-Project.conf");
        var reversed = config.reverse();
        trySaveAndThenUpdate(reversed, AllFields.summary, AllFields.description);
    }

    @Test
    public void taskWithRemoteIdIsUpdatedInJIRA() throws IOException, ConnectorException {
        var jiraMspConfig = ConfigLoader.loadConfig(tempFolder.getRoot(), "Atlassian-Jira_Microsoft-Project_3.conf");
        var toJIRAConfig = jiraMspConfig.reverse();
        trySaveAndThenUpdate(toJIRAConfig, AllFields.summary);
    }

    @Test
    public void taskWithRemoteIdIsUpdatedInGitHub() throws IOException, ConnectorException {
        var config = ConfigLoader.loadConfig(tempFolder.getRoot(), "Github_Microsoft-Project_1.conf");
        var reversedConfig = config.reverse();
        trySaveAndThenUpdate(reversedConfig, AllFields.summary);
    }

    private void trySaveAndThenUpdate(UISyncConfig uiSyncConfig, Field<String> summaryField) throws IOException, ConnectorException {
        var builder = new GTaskBuilder().withRandom(summaryField);
        var task = builder.build();
        task.setId(123l);
        task.setKey("123");
        task.setSourceSystemId(new TaskId(123l, "123"));

        trySaveAndThenUpdate(uiSyncConfig, summaryField, task);
    }

    private void trySaveAndThenUpdate(UISyncConfig uiSyncConfig, Field<String> summaryField,
                                      Field<String> secondField) {
        var builder = new GTaskBuilder().withRandom(summaryField);
        var task = builder.build();
        task.setId(123L);
        task.setKey("123");
        task.setSourceSystemId(new TaskId(123L, "123"));
        task.setValue(secondField, "some value");
        trySaveAndThenUpdate(uiSyncConfig, summaryField, task);
    }

    private void trySaveAndThenUpdate(UISyncConfig uiSyncConfig, Field<String> summaryField, GTask task) {
        var firstResult = uiSyncConfig.saveTasks(List.of(task), ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(firstResult.hasErrors()).isFalse();
        assertThat(firstResult.getCreatedTasksNumber()).isEqualTo(1);
        assertThat(firstResult.getUpdatedTasksNumber()).isEqualTo(0);

        task.setValue(AllFields.summary, "updated summary");
        var secondResult = uiSyncConfig.saveTasks(List.of(task), ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(secondResult.hasErrors()).isFalse();
        assertThat(secondResult.getCreatedTasksNumber()).isEqualTo(0);
        assertThat(secondResult.getUpdatedTasksNumber()).isEqualTo(1);
    }
}
