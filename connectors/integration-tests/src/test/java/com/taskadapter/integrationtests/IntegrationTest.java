package com.taskadapter.integrationtests;

import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.common.DefaultValueSetter;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPSupportedFields;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.connector.redmine.GTaskToRedmine;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.redmine.RedmineSupportedFields;
import com.taskadapter.connector.redmine.RedmineTaskSaver;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.core.RemoteIdUpdater;
import com.taskadapter.core.TaskLoader;
import com.taskadapter.core.TaskSaver;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;

import static com.taskadapter.integrationtests.MSPConfigLoader.generateTemporaryConfig;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class IntegrationTest {

    private static final ProgressMonitor DUMMY_MONITOR = ProgressMonitorUtils.getDummyMonitor();
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private static String projectKey;
    private static RedmineManager mgr;
    private static Project redmineProject;

    @BeforeClass
    public static void oneTimeSetUp() {
        WebServerInfo serverInfo = RedmineTestConfig.getRedmineTestConfig().getServerInfo();
        logger.info("Running Redmine tests with: " + serverInfo);
        mgr = new RedmineManager(serverInfo.getHost(), serverInfo.getApiKey());

        Project project = new Project();
        project.setName("integration tests");
        project.setIdentifier("itest"
                + Calendar.getInstance().getTimeInMillis());
        try {
            redmineProject = mgr.createProject(project);
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
                mgr.deleteProject(projectKey);
                logger.info("Deleted temporary Redmine project with ID " + projectKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("can't delete the test project '" + projectKey + ". reason: "
                    + e.getMessage());
        }
    }

    // this test is ignored because "load" operation does not set remote id anymore,
    // it's now done as a part of "save" (as it should have been from the beginning!)
    // so this test has to be rewritten.
    @Ignore
    @Test
    public void testSaveRemoteId() throws IOException {
        RedmineConfig config = RedmineTestConfig.getRedmineTestConfig();
        config.setProjectKey(projectKey);
        // XXX query id is hardcoded
        config.setQueryId(1);

//        TaskSaver.save(msProjectConnector, mspConfig,
//                redmineConnector, RedmineConnector.ID,
//                "Redmine target location", redmineMappings, loadedTasks,
//                DUMMY_MONITOR);

//		RedmineConnector redmine = new RedmineConnector(config);

//		MSPConfig mspConfigTo = getConfig("non-linear-uuid.xml");
//		MSPTaskSaver saver = new MSPTaskSaver(mspConfigTo);

//		SyncRunner runner = new SyncRunner();
//		List<GTask> load = runner.load(null);
//		for (GTask gTask : load) {
//			assertNotNull(gTask.getRemoteId());
//			assertFalse(gTask.getRemoteId().isEmpty());
//		}
    }

    @Test
    public void testSaveRemoteIdWithNonLinearUUID() throws URISyntaxException, IOException, ConnectorException {

        MSPConfig mspConfig = generateTemporaryConfig("com/taskadapter/integrationtests/non-linear-uuid.xml");
        Connector<?> msProjectConnector = new MSPConnector(mspConfig);

        Mappings mspMappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        mspMappings.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, true, MSPUtils.getDefaultRemoteIdMapping(), "default remote ID");
        RedmineConfig redmineConfig = RedmineTestConfig.getRedmineTestConfig();
        redmineConfig.setProjectKey(projectKey);
        Mappings redmineMappings = TestMappingUtils.fromFields(RedmineSupportedFields.SUPPORTED_FIELDS);

        // load from MSP
        int maxTasksNumber = 999999;
        List<GTask> loadedTasks = TaskLoader.loadTasks(maxTasksNumber, msProjectConnector, "msp1", mspMappings, DUMMY_MONITOR);
        // save to Redmine. this should save the remote IDs
        GTaskToRedmine converter = new GTaskToRedmine(redmineConfig, redmineMappings, null, redmineProject, null, null, null);
        BasicIssueSaveAPI<Issue> redmineTaskSaver = new RedmineTaskSaver(mgr, redmineProject, redmineConfig);
        final TaskSaveResult result =  TaskSavingUtils.saveTasks(loadedTasks,
                converter, redmineTaskSaver,
                DUMMY_MONITOR,
                new DefaultValueSetter(redmineMappings)).getResult();

        assertEquals("must have created 2 tasks", 2, result.getCreatedTasksNumber());
        RemoteIdUpdater.updateRemoteIds(result.getIdToRemoteKeyMap(),
                mspMappings, msProjectConnector);

        //reload from MSP file
        List<GTask> tasksReloadedFromMSPFile = TaskLoader.loadTasks(maxTasksNumber, msProjectConnector, "msp2", mspMappings, DUMMY_MONITOR);

        assertEquals(2, tasksReloadedFromMSPFile.size());

        for (GTask gTask : tasksReloadedFromMSPFile) {
            assertNotNull(gTask.getRemoteId());
            assertFalse(gTask.getRemoteId().isEmpty());
        }
    }

    @Test
    public void testOneSideDisconnectedRelationships() throws IOException, ConnectorException {
        RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();
        redmineConfigTo.setProjectKey(projectKey);

        MSPConfig mspConfig = generateTemporaryConfig("com/taskadapter/integrationtests/ProjectWithOneSideDisconnectedRelationships.xml");
        Connector<?> projectConnector = new MSPConnector(mspConfig);

        Mappings mspMappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        int maxTasksNumber = 999999;
        List<GTask> loadedTasks = TaskLoader.loadTasks(maxTasksNumber, projectConnector, "project1", mspMappings, DUMMY_MONITOR);
        try {
            // save to Redmine
            Mappings redmineMappings = TestMappingUtils.fromFields(RedmineSupportedFields.SUPPORTED_FIELDS);
            GTaskToRedmine converter = new GTaskToRedmine(redmineConfigTo, redmineMappings, null, redmineProject, null, null, null);
            BasicIssueSaveAPI<Issue> redmineTaskSaver = new RedmineTaskSaver(mgr, redmineProject, redmineConfigTo);
            TaskSavingUtils.saveTasks(loadedTasks,
                    converter, redmineTaskSaver,
                    DUMMY_MONITOR,
                    new DefaultValueSetter(redmineMappings));
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
    }
}
