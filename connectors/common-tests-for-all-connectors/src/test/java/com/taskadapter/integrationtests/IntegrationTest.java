package com.taskadapter.integrationtests;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.core.SyncRunner;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.model.GTask;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

public class IntegrationTest extends AbstractSyncRunnerTest {

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
    public void testSaveRemoteIdWithNonLinearUUID() throws URISyntaxException, IOException {

        RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();

        MSPConfig mspConfig = getConfig("non-linear-uuid.xml");
        Connector<?> msProjectConnector = new MSPConnector(mspConfig);

        SyncRunner runner = new SyncRunner(new LicenseManager()); //LicenseManager with license of some type can be set
        runner.setConnectorFrom(msProjectConnector);
        runner.load(ProgressMonitorUtils.getDummyMonitor());

        runner.setDestination(new RedmineConnector(redmineConfigTo));
        runner.save(null);

        //reload from MSP file
        List<GTask> tasks = runner.load(null);

        assertEquals(2, tasks.size());

        for (GTask gTask : tasks) {
            assertNotNull(gTask.getRemoteId());
            assertFalse(gTask.getRemoteId().isEmpty());
        }
    }

    @Test
    public void testOneSideDisconnectedRelationships() throws IOException {
        RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();

        MSPConfig mspConfig = getConfig("ProjectWithOneSideDisconnectedRelationships.xml");
        Connector<?> projectConnector = new MSPConnector(mspConfig);

        SyncRunner runner = new SyncRunner(new LicenseManager()); //LicenseManager with license of some type can be set
        runner.setConnectorFrom(projectConnector);
        runner.setDestination(new RedmineConnector(redmineConfigTo));
        // load from MSP
        runner.load(ProgressMonitorUtils.getDummyMonitor());
        try {
            // save to Redmine
            runner.save(null);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.toString());
        }

    }
}
