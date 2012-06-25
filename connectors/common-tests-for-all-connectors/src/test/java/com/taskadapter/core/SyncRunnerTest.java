package com.taskadapter.core;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.integrationtests.AbstractSyncRunnerTest;
import com.taskadapter.integrationtests.RedmineTestConfig;
import com.taskadapter.license.LicenseManager;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class SyncRunnerTest extends AbstractSyncRunnerTest {

    @Test
    /**
     * This test should grant that Tasks ARE read as a tree by the SyncRunner. The code that does that
     * was moved to save(), but it cannot be done because the tree is not shown to the user.
     */
    public void testLoadAsTree() throws URISyntaxException, IOException, ConnectorException {

        RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();

        MSPConfig mspConfig = getConfig("ProjectWithTree.xml");
        Connector<?> projectConnector = new MSPConnector(mspConfig);

        SyncRunner runner = new SyncRunner(new LicenseManager()); //LicenseManager with license of some type can be set
        runner.setConnectorFrom(projectConnector);
        runner.setDestination(new RedmineConnector(redmineConfigTo));
        // load from MSP
        runner.load(ProgressMonitorUtils.getDummyMonitor());

        assertEquals(1, runner.getTasks().size());

    }

}
