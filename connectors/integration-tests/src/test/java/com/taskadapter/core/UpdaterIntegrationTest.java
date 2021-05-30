package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPTaskSaver;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineToGTask;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.test.core.IntegrationTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class UpdaterIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdaterIntegrationTest.class);
    private static String projectKey;
    private static int projectId;
    private static RedmineManager mgr;


    private static final int TASKS_NUMBER = 1;

    private List<GTask> rmIssues;

    private NewConnector redmineConnector;
    private RedmineConfig redmineConfig;
    private FileSetup setup;
    private NewConnector projectConnector;
//    static WebConnectorSetup setup = RedmineTestConfig.getRedmineServerInfo();

    // TODO TA3 integration tests

    @BeforeClass
    public static void oneTimeSetUp() {
//        logger.info("Running Redmine tests with: " + setup);
//        mgr = RedmineManagerFactory.createRedmineManager(setup);

        try {
            Project project = new Project(mgr.getTransport(), "integration test project",
                    "ittest" + Calendar.getInstance().getTimeInMillis())
                    .create();

            projectKey = project.getIdentifier();
            projectId = project.getId();
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
//        redmineConfig = RedmineTestConfig.getRedmineTestConfig();
//        redmineConfig.setProjectKey(projectKey);
//        redmineConnector = new RedmineConnector(redmineConfig, RedmineTestConfig.getRedmineServerInfo());
        setup = createTempMSPSetup();
        projectConnector = new MSPConnector(setup);
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

    private void saveToMSP(Iterable<FieldRow<?>> rows) throws ConnectorException {
        new MSPTaskSaver(setup, rows).saveData(rmIssues);
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

/*
    private List<GTask> createRedmineIssues(int issuesNumber) {
        List<GTask> issues = new ArrayList<>(issuesNumber);
        List<Issue> issuesToCreate = generateRedmineIssues(issuesNumber);
        RedmineUserCache cache = new RedmineUserCache();
        RedmineToGTask converter = new RedmineToGTask(redmineConfig, cache);
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
*/

    private List<Issue> generateRedmineIssues(int issuesNumber) {
        List<Issue> issues = new ArrayList<>(issuesNumber);
        for (int i = 0; i < issuesNumber; i++) {
            Issue issue = new Issue();
            issue.setProjectId(projectId);
            issue.setSubject("some issue " + i + " " + new Date());
            issue.setEstimatedHours((float) i);
            issues.add(issue);
        }
        return issues;
    }

    private FileSetup createTempMSPSetup() {
        File temp;
        try {
            temp = File.createTempFile("pattern", ".xml");
        } catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }
        temp.deleteOnExit();

        return FileSetup.apply(MSPConnector.ID, "label", temp.getAbsolutePath(), temp.getAbsolutePath());
    }

/*
    private Mappings getMSPMappingsWithRemoteIdSet() {
        Mappings mappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        mappings.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, true, TaskField.TEXT22.toString(), "default remote ID");
        return mappings;
    }
*/
}
