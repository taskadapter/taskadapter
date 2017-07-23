package com.taskadapter.integrationtests;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static junit.framework.Assert.fail;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class IntegrationTest {

    // TODO TA3 integration tests

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private static String projectKey;
    private static RedmineManager mgr;
    private static Project redmineProject;

    @BeforeClass
    public static void oneTimeSetUp() {
        WebServerInfo serverInfo = RedmineTestConfig.getRedmineServerInfo();
        logger.info("Running Redmine tests with: " + serverInfo);
        mgr = RedmineManagerFactory.createRedmineManager(serverInfo);

        Project project = ProjectFactory.create("integration tests",
                "itest" + Calendar.getInstance().getTimeInMillis());
        try {
            redmineProject = mgr.getProjectManager().createProject(project);
            projectKey = redmineProject.getIdentifier();
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
            Assert.fail("can't delete the test project '" + projectKey + ". reason: "
                    + e.getMessage());
        }
    }

    /**
     * regression test for https://bitbucket.org/taskadapter/taskadapter/issues/43/tasks-are-not-updated-in-redmine-404-not
     */
/*
    @Test
    public void taskWithRemoteIdIsUpdatedInRedmine() throws Exception {
        RedmineConfig redmineConfig = RedmineTestConfig.getRedmineTestConfig();
        redmineConfig.setProjectKey(projectKey);
        // XXX query id is hardcoded
        redmineConfig.setQueryId(1);
        RedmineConnector redmine = new RedmineConnector(redmineConfig);

        List<GTask> gTasks = TestUtils.generateTasks(1);
        final TaskSaveResult saveResult = TaskSaver.save(redmine, "target redmine", redmineMappings, gTasks, ProgressMonitorUtils.DUMMY_MONITOR);

        final String key = saveResult.getRemoteKeys().iterator().next();

        final GTask createdTask = gTasks.get(0);
        createdTask.setSourceSystemId(key);
        createdTask.setSummary("updated summary");

        final TaskSaveResult secondResult = TaskSaver.save(redmine, "target redmine", redmineMappings, gTasks, ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(secondResult.hasErrors()).isFalse();
        assertThat(secondResult.getCreatedTasksNumber()).isEqualTo(0);
        assertThat(secondResult.getUpdatedTasksNumber()).isEqualTo(1);
    }
*/

/*
    @Test
    public void testSaveRemoteIdWithNonLinearUUID() throws URISyntaxException, IOException, ConnectorException {

        MSPConfig mspConfig = generateTemporaryConfig("com/taskadapter/integrationtests/non-linear-uuid.xml");
        NewConnector msProjectConnector = new MSPConnector(mspConfig);

        Mappings mspMappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        mspMappings.setMapping(GTaskDescriptor.FIELD.SOURCE_SYSTEM_ID, true, MSPUtils.getDefaultRemoteIdMapping(), "default remote ID");
        RedmineConfig redmineConfig = RedmineTestConfig.getRedmineTestConfig();
        redmineConfig.setProjectKey(projectKey);
        Collection<GTaskDescriptor.FIELD> fields = RedmineSupportedFields.SUPPORTED_FIELDS.getSupportedFields();

        Map<String, String> defaultValuesForEmptyFields = Collections.emptyMap();

        // load from MSP
        int maxTasksNumber = 999999;
        List<GTask> loadedTasks = TaskLoader.loadTasks(maxTasksNumber, msProjectConnector, "msp1", mspMappings, ProgressMonitorUtils.DUMMY_MONITOR);
        // save to Redmine. this should save the remote IDs
        GTaskToRedmine converter = new GTaskToRedmine(redmineConfig,
                fields,
                Collections.emptyMap(), redmineProject, null, null, null);
        BasicIssueSaveAPI<Issue> redmineTaskSaver = new RedmineTaskSaver(mgr.getIssueManager(), redmineConfig);
        final TaskSaveResult result =  TaskSavingUtils.saveTasks(loadedTasks,
                converter, redmineTaskSaver,
                ProgressMonitorUtils.DUMMY_MONITOR,
                new DefaultValueSetter(defaultValuesForEmptyFields)).getResult();

        assertEquals("must have created 2 tasks", 2, result.getCreatedTasksNumber());
        RemoteIdUpdater.updateRemoteIds(result.getIdToRemoteKeyMap(),
                mspMappings, msProjectConnector);

        //reload from MSP file
        List<GTask> tasksReloadedFromMSPFile = TaskLoader.loadTasks(maxTasksNumber, msProjectConnector, "msp2", mspMappings, ProgressMonitorUtils.DUMMY_MONITOR);

        assertEquals(2, tasksReloadedFromMSPFile.size());

        for (GTask gTask : tasksReloadedFromMSPFile) {
            assertNotNull(gTask.getSourceSystemId());
            assertFalse(gTask.getSourceSystemId().isEmpty());
        }
    }

    @Test
    public void testOneSideDisconnectedRelationships() throws IOException, ConnectorException {
        RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();
        redmineConfigTo.setProjectKey(projectKey);

        MSPConfig mspConfig = generateTemporaryConfig("com/taskadapter/integrationtests/ProjectWithOneSideDisconnectedRelationships.xml");
        NewConnector projectConnector = new MSPConnector(mspConfig);

        int maxTasksNumber = 999999;
        List<GTask> loadedTasks = TaskLoader.loadTasks(maxTasksNumber, projectConnector, "project1", ProgressMonitorUtils.DUMMY_MONITOR);
        try {
            // save to Redmine
            Map<String, String> defaultValuesForEmptyFields = Collections.emptyMap();

            GTaskToRedmine converter = new GTaskToRedmine(redmineConfigTo,
                    Collections.emptyMap(), redmineProject, null, null, null, null);
            BasicIssueSaveAPI<Issue> redmineTaskSaver = new RedmineTaskSaver(mgr.getIssueManager(), redmineConfigTo);
            TaskSavingUtils.saveTasks(loadedTasks,
                    converter, redmineTaskSaver,
                    ProgressMonitorUtils.DUMMY_MONITOR,
                    rows);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
    }*/
}
