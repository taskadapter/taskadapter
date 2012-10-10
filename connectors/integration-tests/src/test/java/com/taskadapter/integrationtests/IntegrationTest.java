package com.taskadapter.integrationtests;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.DefaultMSPMappings;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.connector.redmine.DefaultRedmineMappings;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.core.TaskLoader;
import com.taskadapter.core.TaskSaver;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.taskadapter.integrationtests.MSPConfigLoader.generateTemporaryConfig;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class IntegrationTest {

    public static final ProgressMonitor DUMMY_MONITOR = ProgressMonitorUtils.getDummyMonitor();

    // this test is ignored because "load" operation does not set remote id anymore,
    // it's now done as a part of "save" (as it should have been from the beginning!)
    // so this test has to be rewritten.
    @Ignore
    @Test
    public void testSaveRemoteId() throws IOException {
//		RedmineConfig config = RedmineTestConfig.getRedmineTestConfig();
//		config.setProjectKey("test");
        // XXX query id is hardcoded!!!
//		config.setQueryId(1);

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

        RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();

        MSPConfig mspConfig = generateTemporaryConfig("com/taskadapter/integrationtests/non-linear-uuid.xml");
        Connector<?> msProjectConnector = new MSPConnector(mspConfig);

        Mappings mspMappings = DefaultMSPMappings.generate();
        mspMappings.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, true, MSPUtils.getDefaultRemoteIdMapping());
        List<GTask> loadedTasks = TaskLoader.loadTasks(new LicenseManager(), msProjectConnector, mspMappings, DUMMY_MONITOR);

        RedmineConnector redmineConnector = new RedmineConnector(redmineConfigTo);
        Mappings redmineMappings = DefaultRedmineMappings.generate();
        TaskSaver.save(MSPConnector.ID, redmineConnector, RedmineConnector.ID, redmineMappings, loadedTasks, DUMMY_MONITOR);

        //reload from MSP file
        List<GTask> tasksReloadedFromMSPFile = TaskLoader.loadTasks(new LicenseManager(), msProjectConnector, mspMappings, DUMMY_MONITOR);

        assertEquals(2, tasksReloadedFromMSPFile.size());

        for (GTask gTask : tasksReloadedFromMSPFile) {
            assertNotNull(gTask.getRemoteId());
            assertFalse(gTask.getRemoteId().isEmpty());
        }
    }

    @Test
    public void testOneSideDisconnectedRelationships() throws IOException, ConnectorException {
        RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();

        MSPConfig mspConfig = generateTemporaryConfig("com/taskadapter/integrationtests/ProjectWithOneSideDisconnectedRelationships.xml");
        Connector<?> projectConnector = new MSPConnector(mspConfig);

//        SyncRunner runner = new SyncRunner(new LicenseManager()); //LicenseManager with license of some type can be set
//        runner.setConnectorFrom(projectConnector, MSPConnector.ID);
//        runner.setDestination(, RedmineConnector.ID);
//        // load from MSP
//        runner.load(ProgressMonitorUtils.getDummyMonitor());
//
        Mappings mspMappings = DefaultMSPMappings.generate();
        List<GTask> loadedTasks = TaskLoader.loadTasks(new LicenseManager(), projectConnector, mspMappings, DUMMY_MONITOR);

        try {
            // save to Redmine
            Mappings redmineMappings = DefaultRedmineMappings.generate();
            RedmineConnector redmineConnector = new RedmineConnector(redmineConfigTo);
            TaskSaver.save(MSPConnector.ID, redmineConnector, RedmineConnector.ID, redmineMappings, loadedTasks, DUMMY_MONITOR);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
    }
}
