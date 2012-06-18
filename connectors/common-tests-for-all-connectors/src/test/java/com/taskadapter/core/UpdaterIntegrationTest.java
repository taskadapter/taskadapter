package com.taskadapter.core;

import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPTaskLoader;
import com.taskadapter.connector.msp.MSPTaskSaver;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.redmine.RedmineDataConverter;
import com.taskadapter.integrationtests.AbstractSyncRunnerTest;
import com.taskadapter.integrationtests.RedmineTestConfig;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class UpdaterIntegrationTest extends AbstractSyncRunnerTest {

    private static final int TASKS_NUMBER = 1;

    private List<GTask> rmIssues;

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

    @Test
    public void testMSPFileIsUpdated() throws Exception {
		createTasksInRedmine();
        saveToMSP();
        modifyRedmineData();
        updateMSPFile();
        verifyMSPData();
    }

    private void updateMSPFile() {
        Updater updater = new Updater(projectConnector, redmineConnector);
        updater.start();
    }

	private void createTasksInRedmine() {
		this.rmIssues = createRedmineIssues(projectKey, TASKS_NUMBER);
		TaskUtil.setRemoteIdField(rmIssues);
	}

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
        List<GTask> mspTasks = mspLoader.loadTasks(mspConfig);
        assertEquals(TASKS_NUMBER, mspTasks.size());
        for (GTask task : mspTasks) {
            GTask rmTask = TestUtils.findTaskByKey(rmIssues, task.getRemoteId());
            assertEquals(rmTask.getSummary(), task.getSummary());
            assertEquals(rmTask.getEstimatedHours(), task.getEstimatedHours());
        }
    }

    private List<GTask> createRedmineIssues(String projectKey, int issuesNumber) {
        List<GTask> issues = new ArrayList<GTask>(issuesNumber);
        RedmineManager mgr = new RedmineManager(redmineConfig.getServerInfo().getHost());
        mgr.setLogin(redmineConfig.getServerInfo().getUserName());
        mgr.setPassword(redmineConfig.getServerInfo().getPassword());

        List<Issue> issuesToCreate = generateRedmineIssues(issuesNumber);

        RedmineDataConverter converter = new RedmineDataConverter(redmineConfig);
        for (Issue anIssuesToCreate : issuesToCreate) {
            Issue issue;
            try {
                issue = mgr.createIssue(projectKey, anIssuesToCreate);
            } catch (Exception e) {
                throw new RuntimeException(e.toString(), e);
            }
            GTask task = converter.convertToGenericTask(issue);
            issues.add(task);
        }

        return issues;

    }

    private List<Issue> generateRedmineIssues(int issuesNumber) {
        List<Issue> issues = new ArrayList<Issue>(issuesNumber);
        for (int i = 0; i < issuesNumber; i++) {
            Issue issue = new Issue();
            issue.setSubject("some issue " + i + " " + new Date());
            issue.setEstimatedHours((float) i);
            issues.add(issue);
        }
        return issues;
    }

}
