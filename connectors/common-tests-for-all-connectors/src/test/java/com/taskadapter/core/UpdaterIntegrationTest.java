package com.taskadapter.core;

import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPTaskLoader;
import com.taskadapter.connector.msp.MSPTaskSaver;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.integrationtests.AbstractSyncRunnerTest;
import com.taskadapter.integrationtests.RedmineTestConfig;
import com.taskadapter.model.GTask;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class UpdaterIntegrationTest extends AbstractSyncRunnerTest {

    private static final int TASKS_NUMBER = 1;

    private List<GTask> rmIssues;
    private List<GTask> mspTasks;

    private Connector<?> redmineConnector;
    private String projectKey;
    private RedmineConfig redmineConfig;
    private MSPConfig mspConfig;
    private Connector<?> projectConnector;

    @Before
    public void init() {
        redmineConfig = RedmineTestConfig.getRedmineTestConfig();
        redmineConnector = new RedmineConnector(redmineConfig);
        projectKey = redmineConfig.getProjectKey();
        mspConfig = createTempMSPConfig();
        projectConnector = new MSPConnector(mspConfig);
    }

    // TODO fix after the code can be compiled OK.
    // RedmineUtils can't be accessed from this context (merged from Eclipse branch)
    @Ignore
    @Test
    public void testMSPFileIsUpdated() throws Exception {
        // fix this
//		createTasksInRedmine();
        saveToMSP();
        modifyRedmineData();
        updateMSPFile();
        verifyMSPData();
    }

    private void updateMSPFile() {
        Updater updater = new Updater(projectConnector, redmineConnector);
        updater.start();
    }

//	private void createTasksInRedmine() {
//		this.rmIssues = RedmineUtils.createIssues(redmineConfig, projectKey, TASKS_NUMBER);
//		TaskUtil.setRemoteIdField(rmIssues);
//	}

    private void saveToMSP() {
        new MSPTaskSaver(mspConfig).saveData(rmIssues, null);
    }

    private void modifyRedmineData() {
        Random r = new Random();
        for (GTask task : rmIssues) {
            String updatedSummary = "updated" + r.nextInt();
            task.setSummary(updatedSummary);
            Float oldGoodTime = task.getEstimatedHours();
            if (oldGoodTime == null) {
                oldGoodTime = 0f;
            }
            task.setEstimatedHours(oldGoodTime + 5);
        }
        redmineConnector.saveData(rmIssues, null);
    }

    private void verifyMSPData() throws Exception {
        MSPTaskLoader mspLoader = new MSPTaskLoader();
        mspTasks = mspLoader.loadTasks(mspConfig);
        assertEquals(TASKS_NUMBER, mspTasks.size());
        for (GTask task : mspTasks) {
            GTask rmTask = TestUtils.findTaskByKey(rmIssues, task.getRemoteId());
            assertEquals(rmTask.getSummary(), task.getSummary());
            assertEquals(rmTask.getEstimatedHours(), task.getEstimatedHours());
        }
    }

}
