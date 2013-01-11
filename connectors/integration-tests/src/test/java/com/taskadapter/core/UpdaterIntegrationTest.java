package com.taskadapter.core;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPSupportedFields;
import com.taskadapter.connector.msp.MSPTaskSaver;
import com.taskadapter.connector.redmine.*;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.integrationtests.RedmineTestConfig;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import net.sf.mpxj.TaskField;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UpdaterIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdaterIntegrationTest.class);
    private static String projectKey;
    private static RedmineManager mgr;


    private static final int TASKS_NUMBER = 1;

    private List<GTask> rmIssues;

    private Connector<?> redmineConnector;
    private RedmineConfig redmineConfig;
    private MSPConfig mspConfig;
    private Connector<?> projectConnector;

    @BeforeClass
    public static void oneTimeSetUp() {
        WebServerInfo serverInfo = RedmineTestConfig.getRedmineTestConfig().getServerInfo();
        logger.info("Running Redmine tests with: " + serverInfo);
        mgr = new RedmineManager(serverInfo.getHost(), serverInfo.getUserName(), serverInfo.getPassword());

        Project project = new Project();
        project.setName("integration test project");
        project.setIdentifier("ittest"
                + Calendar.getInstance().getTimeInMillis());
        try {
            Project createdProject = mgr.createProject(project);
            projectKey = createdProject.getIdentifier();
            logger.info("Created temporary Redmine project with key " + projectKey);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        try {
            if (mgr != null) {
                mgr.deleteProject(projectKey);
                logger.info("Deleted temporary Redmine project with ID " + projectKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("can't delete the test project '" + projectKey + ". reason: "
                    + e.getMessage());
        }
    }

    @Before
    public void beforeEachTest() {
        redmineConfig = RedmineTestConfig.getRedmineTestConfig();
        redmineConfig.setProjectKey(projectKey);
        redmineConnector = new RedmineConnector(redmineConfig);
        mspConfig = createTempMSPConfig();
        projectConnector = new MSPConnector(mspConfig);
    }

    @Test
    public void testMSPFileIsUpdated() throws Exception {
        createTasksInRedmine();
        Mappings mspMappingsWithRemoteIdSet = getMSPMappingsWithRemoteIdSet();
        saveToMSP(mspMappingsWithRemoteIdSet);

        Mappings redmineMappings = TestMappingUtils.fromFields(RedmineSupportedFields.SUPPORTED_FIELDS);
        modifyRedmineData(redmineMappings);

        updateMSPFile(mspMappingsWithRemoteIdSet, redmineMappings);
        verifyMSPData(mspMappingsWithRemoteIdSet);
    }

    private void updateMSPFile(Mappings mspMappingsWithRemoteIdSet, Mappings redmineMappings) throws ConnectorException {
        Updater updater = new Updater(projectConnector, mspMappingsWithRemoteIdSet, redmineConnector, redmineMappings, "someTestData");
        updater.loadTasksFromFile(ProgressMonitorUtils.getDummyMonitor());
        updater.removeTasksWithoutRemoteIds();
        updater.loadExternalTasks();
        updater.saveFile();
    }

    private void createTasksInRedmine() {
        this.rmIssues = createRedmineIssues(projectKey, TASKS_NUMBER);
        TaskUtil.setRemoteIdField(rmIssues);
    }

    private void saveToMSP(Mappings mappings) throws ConnectorException {
        new MSPTaskSaver(mspConfig, mappings,
                ProgressMonitorUtils.getDummyMonitor()).saveData(rmIssues);
    }

    private void modifyRedmineData(Mappings mappings) throws ConnectorException {
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
        redmineConnector.saveData(rmIssues, null, mappings);
    }

    private void verifyMSPData(Mappings mappings) throws Exception {
        final MSPConnector connector = new MSPConnector(mspConfig);
        List<GTask> mspTasks = ConnectorUtils.loadDataOrderedById(connector, mappings);
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

        RedmineToGTask converter = new RedmineToGTask(redmineConfig);
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

    private MSPConfig createTempMSPConfig() {

        File temp;
        try {
            temp = File.createTempFile("pattern", ".suffix");
        } catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }
        temp.deleteOnExit();

        MSPConfig mspConfig = new MSPConfig();
        mspConfig.setInputAbsoluteFilePath(temp.getAbsolutePath());
        mspConfig.setOutputAbsoluteFilePath(temp.getAbsolutePath());
        return mspConfig;
    }

    private Mappings getMSPMappingsWithRemoteIdSet() {
        Mappings mappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        mappings.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, true, TaskField.TEXT22.toString());
        return mappings;
    }
}
