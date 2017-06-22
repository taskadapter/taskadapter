package com.taskadapter.web.uiapi;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.GTask;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This is a FULL-stack test using the same class as UI when "export" button is clicked.
 */
public class UISyncConfigIT {

    // TODO maybe use temporary projects in Redmine and JIRA?

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private UISyncConfig config;
    private UISyncConfig toRedmineConfig;

    @Before
    public void beforeEachTest() throws IOException {
        config = ConfigLoader.loadConfig("Redmine_Microsoft-Project_3.ta_conf");
        toRedmineConfig = config.reverse();
    }

    @Test
    public void tasksCanBeSavedToRedmine() throws Exception {
        List<GTask> gTasks = TestUtils.generateTasks(1);
        final UISyncConfig.TaskExportResult taskExportResult = toRedmineConfig.saveTasks(gTasks, ProgressMonitorUtils.DUMMY_MONITOR);
        final TaskSaveResult saveResult = taskExportResult.saveResult;
        assertThat(saveResult.hasErrors()).isFalse();
        assertThat(saveResult.getCreatedTasksNumber()).isEqualTo(1);
        assertThat(saveResult.getUpdatedTasksNumber()).isEqualTo(0);
    }

    @Test
    public void tasksCanBeLoadedFromJiraAndSavedToRedmine() throws Exception {
        UISyncConfig config = ConfigLoader.loadConfig("Atlassian-JIRA_Redmine.ta_conf");
        List<GTask> loadedTasks = config.loadTasks(100);
        assertThat(loadedTasks.size()).isGreaterThan(0);
        final UISyncConfig.TaskExportResult taskExportResult = toRedmineConfig.saveTasks(loadedTasks, ProgressMonitorUtils.DUMMY_MONITOR);
        final TaskSaveResult saveResult = taskExportResult.saveResult;
        assertThat(saveResult.hasErrors()).isFalse();
        assertThat(saveResult.getCreatedTasksNumber()).isEqualTo(loadedTasks.size());
    }

    /**
     * regression test for https://bitbucket.org/taskadapter/taskadapter/issues/43/tasks-are-not-updated-in-redmine-404-not
     */
    @Test
    public void taskWithRemoteIdIsUpdatedInRedmine() throws Exception {
        final UISyncConfig toRedmineConfig = config.reverse();
        trySaveAndThenUpdate(toRedmineConfig);
    }

    /**
     * regression test for https://bitbucket.org/taskadapter/taskadapter/issues/43/tasks-are-not-updated-in-redmine-404-not
     */
    @Test
    public void taskWithRemoteIdIsUpdatedInMantisBT() throws Exception {
        final UISyncConfig config = ConfigLoader.loadConfig("Microsoft-Project_Mantis_1.ta_conf");
        trySaveAndThenUpdate(config);
    }
    /**
     * regression test for https://bitbucket.org/taskadapter/taskadapter/issues/43/tasks-are-not-updated-in-redmine-404-not
     */
    @Test
    public void taskWithRemoteIdIsUpdatedInJIRA() throws Exception {
        UISyncConfig jiraMspConfig = ConfigLoader.loadConfig("Atlassian-Jira_Microsoft-Project_3.ta_conf");
        final UISyncConfig toJIRAConfig = jiraMspConfig.reverse();
        trySaveAndThenUpdate(toJIRAConfig);
    }

    /**
     * regression test for https://bitbucket.org/taskadapter/taskadapter/issues/43/tasks-are-not-updated-in-redmine-404-not
     */
    @Test
    public void taskWithRemoteIdIsUpdatedInGitHub() throws Exception {
        UISyncConfig config = ConfigLoader.loadConfig("Github_Microsoft-Project_1.ta_conf");
        final UISyncConfig reversedConfig = config.reverse();
        trySaveAndThenUpdate(reversedConfig);
    }

    private static void trySaveAndThenUpdate(UISyncConfig uiSyncConfig) {
        List<GTask> tasks = TestUtils.generateTasks(1);
        final UISyncConfig.TaskExportResult taskExportResult = uiSyncConfig.saveTasks(tasks, ProgressMonitorUtils.DUMMY_MONITOR);
        final TaskSaveResult saveResult = taskExportResult.saveResult;

        final String key = saveResult.getRemoteKeys().iterator().next();
        final GTask createdTask = tasks.get(0);
        createdTask.setRemoteId(key);
        createdTask.setSummary("updated summary");

        final UISyncConfig.TaskExportResult secondResultWrapper = uiSyncConfig.saveTasks(tasks, ProgressMonitorUtils.DUMMY_MONITOR);
        final TaskSaveResult secondResult = secondResultWrapper.saveResult;
        assertThat(secondResult.hasErrors()).isFalse();
        assertThat(secondResult.getCreatedTasksNumber()).isEqualTo(0);
        assertThat(secondResult.getUpdatedTasksNumber()).isEqualTo(1);
    }
}
