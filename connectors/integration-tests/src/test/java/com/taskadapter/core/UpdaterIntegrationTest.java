package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPTaskSaver;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.redmine.RedmineField;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.connector.redmine.RedmineToGTask;
import com.taskadapter.connector.testlib.InMemoryTaskKeeper;
import com.taskadapter.integrationtests.RedmineTestConfig;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.fail;

public class UpdaterIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdaterIntegrationTest.class);
    private static String projectKey;
    private static int projectId;
    private static RedmineManager mgr;


    private static final int TASKS_NUMBER = 1;

    private List<GTask> rmIssues;

    private NewConnector redmineConnector;
    private RedmineConfig redmineConfig;
    private MSPConfig mspConfig;
    private NewConnector projectConnector;
    private TaskKeeper taskKeeper = new InMemoryTaskKeeper();
    static WebServerInfo webServerInfo = RedmineTestConfig.getRedmineServerInfo();

    @BeforeClass
    public static void oneTimeSetUp() {
        logger.info("Running Redmine tests with: " + webServerInfo);
        mgr = RedmineManagerFactory.createRedmineManager(webServerInfo);

        Project project = ProjectFactory.create("integration test project",
                "ittest" + Calendar.getInstance().getTimeInMillis());
        try {
            Project createdProject = mgr.getProjectManager().createProject(project);
            projectKey = createdProject.getIdentifier();
            projectId = createdProject.getId();
            logger.info("Created temporary Redmine project with key " + projectKey);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        try {
            if (mgr != null) {
                mgr.getProjectManager().deleteProject(projectKey);
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
        redmineConnector = new RedmineConnector(redmineConfig, RedmineTestConfig.getRedmineServerInfo());
        mspConfig = createTempMSPConfig();
        projectConnector = new MSPConnector(mspConfig);
    }

    // TODO TA3 Msp remote ids test
/*

    @Test
    public void testMSPFileIsUpdated() throws Exception {
        createTasksInRedmine();
        Mappings mspMappingsWithRemoteIdSet = getMSPMappingsWithRemoteIdSet();
        saveToMSP(mspMappingsWithRemoteIdSet);

        modifyRedmineData(RedmineField.fieldsAsJava());

        updateMSPFile(mspMappingsWithRemoteIdSet, redmineMappings);
        verifyMSPData(mspMappingsWithRemoteIdSet);
    }

    private void updateMSPFile(Mappings mspMappingsWithRemoteIdSet, Mappings redmineMappings) throws ConnectorException {
        Updater updater = new Updater(projectConnector, mspMappingsWithRemoteIdSet, redmineConnector, redmineMappings, "someTestData");
        updater.loadTasksFromFile(ProgressMonitorUtils.DUMMY_MONITOR);
        updater.removeTasksWithoutRemoteIds();
        updater.loadExternalTasks();
        updater.saveFile();
    }
*/

    private void createTasksInRedmine() {
        this.rmIssues = createRedmineIssues(TASKS_NUMBER);
        TaskUtil.setRemoteIdField(rmIssues);
    }

    private void saveToMSP(List<FieldRow> rows) throws ConnectorException {
        new MSPTaskSaver(mspConfig, rows).saveData(rmIssues);
    }

/*    private void modifyRedmineData(List<FieldRow> rows) throws ConnectorException {
        Random r = new Random();
        for (GTask task : rmIssues) {
            String updatedSummary = "updated" + r.nextInt();
            task.setValue(RedmineField.summary(), updatedSummary);
            Float oldGoodTime = (Float) task.getValue(RedmineField.estimatedTime());
            if (oldGoodTime == null) {
                oldGoodTime = 0f;
            }
            task.setValue(RedmineField.estimatedTime(), oldGoodTime + 5);
        }
        redmineConnector.saveData(taskKeeper, rmIssues, null, rows);
    }*/

/*
    private void verifyMSPData() throws Exception {
        final MSPConnector connector = new MSPConnector(mspConfig);
        List<GTask> mspTasks = ConnectorUtils.loadDataOrderedById(connector);
        assertEquals(TASKS_NUMBER, mspTasks.size());
        for (GTask task : mspTasks) {
            GTask rmTask = TestUtils.findTaskByKey(rmIssues, task.getRemoteId());
            assertEquals(rmTask.getValue(RedmineField.summary()), task.getValue(MspField.summary()));
            assertEquals(rmTask.getValue(RedmineField.estimatedTime()), task.getValue(MspField.estimatedTime()));
        }
    }
*/

    private List<GTask> createRedmineIssues(int issuesNumber) {
        List<GTask> issues = new ArrayList<>(issuesNumber);
        RedmineManager mgr = RedmineManagerFactory.createRedmineManager(webServerInfo);
        List<Issue> issuesToCreate = generateRedmineIssues(issuesNumber);

        RedmineToGTask converter = new RedmineToGTask(redmineConfig);
        for (Issue issueToCreate : issuesToCreate) {
            Issue issue;
            try {
                issue = mgr.getIssueManager().createIssue(issueToCreate);
            } catch (Exception e) {
                throw new RuntimeException(e.toString(), e);
            }
            GTask task = converter.convertToGenericTask(issue);
            issues.add(task);
        }

        return issues;

    }

    private List<Issue> generateRedmineIssues(int issuesNumber) {
        List<Issue> issues = new ArrayList<>(issuesNumber);
        for (int i = 0; i < issuesNumber; i++) {
            Issue issue = new Issue();
            issue.setProject(ProjectFactory.create(projectId));
            issue.setSubject("some issue " + i + " " + new Date());
            issue.setEstimatedHours((float) i);
            issues.add(issue);
        }
        return issues;
    }

    private MSPConfig createTempMSPConfig() {

        File temp;
        try {
            temp = File.createTempFile("pattern", ".xml");
        } catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }
        temp.deleteOnExit();

        MSPConfig mspConfig = new MSPConfig();
        mspConfig.setInputAbsoluteFilePath(temp.getAbsolutePath());
        mspConfig.setOutputAbsoluteFilePath(temp.getAbsolutePath());
        return mspConfig;
    }

/*
    private Mappings getMSPMappingsWithRemoteIdSet() {
        Mappings mappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        mappings.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, true, TaskField.TEXT22.toString(), "default remote ID");
        return mappings;
    }
*/
}
