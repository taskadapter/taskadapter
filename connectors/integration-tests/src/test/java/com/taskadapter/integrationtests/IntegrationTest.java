package com.taskadapter.integrationtests;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.DefaultMSPMappings;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.connector.redmine.DefaultRedmineMappings;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.core.RemoteIdUpdater;
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

    private static final ProgressMonitor DUMMY_MONITOR = ProgressMonitorUtils.getDummyMonitor();

    // this test is ignored because "load" operation does not set remote id anymore,
    // it's now done as a part of "save" (as it should have been from the beginning!)
    // so this test has to be rewritten.
    @Ignore
    @Test
    public void testSaveRemoteId() throws IOException {
		RedmineConfig config = RedmineTestConfig.getRedmineTestConfig();
		config.setProjectKey("test");
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

        Mappings mspMappings = DefaultMSPMappings.generate();
        mspMappings.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, true, MSPUtils.getDefaultRemoteIdMapping());
        RedmineConnector redmineConnector = new RedmineConnector(RedmineTestConfig.getRedmineTestConfig());
        Mappings redmineMappings = DefaultRedmineMappings.generate();
        // TODO !! for Maxim K: this is not quite correct. the RemoteID flag is set in the destination config manually,
        // while it should be set by our NewMapping parser to both source and destination mappings.
        redmineMappings.setMapping(GTaskDescriptor.FIELD.REMOTE_ID, true, MSPUtils.getDefaultRemoteIdMapping());

        // load from MSP
        List<GTask> loadedTasks = TaskLoader.loadTasks(new LicenseManager(), msProjectConnector, "msp1", mspMappings, DUMMY_MONITOR);
        // save to Redmine. this should save the remote IDs
        final TaskSaveResult result = TaskSaver.save(redmineConnector, "Redmine target location",
                redmineMappings, loadedTasks,
                DUMMY_MONITOR);
        RemoteIdUpdater.updateRemoteIds(result.getIdToRemoteKeyMap(),
                mspMappings, msProjectConnector);

        //reload from MSP file
        List<GTask> tasksReloadedFromMSPFile = TaskLoader.loadTasks(new LicenseManager(), msProjectConnector, "msp2", mspMappings, DUMMY_MONITOR);

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

        Mappings mspMappings = DefaultMSPMappings.generate();
        List<GTask> loadedTasks = TaskLoader.loadTasks(new LicenseManager(), projectConnector, "project1", mspMappings, DUMMY_MONITOR);
        try {
            // save to Redmine
            Mappings redmineMappings = DefaultRedmineMappings.generate();
            RedmineConnector redmineConnector = new RedmineConnector(redmineConfigTo);
            TaskSaver.save(redmineConnector, "Redmine target location",
                    redmineMappings,
                    loadedTasks, DUMMY_MONITOR);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }
    }
}
