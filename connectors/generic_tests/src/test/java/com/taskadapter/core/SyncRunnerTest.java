package com.taskadapter.core;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineTaskSaver;
import com.taskadapter.integrationtests.AbstractSyncRunnerTest;
import com.taskadapter.integrationtests.RedmineTestConfig;
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
	public void testLoadAsTree() throws URISyntaxException, IOException {

		RedmineConfig redmineConfigTo = RedmineTestConfig.getRedmineTestConfig();
		RedmineTaskSaver saver = new RedmineTaskSaver(redmineConfigTo);

		MSPConfig mspConfig = getConfig("ProjectWithTree.xml");
		Connector<?> projectConnector = new MSPConnector(mspConfig);

        SyncRunner runner = new SyncRunner();
   		runner.setConnectorFrom(projectConnector);
   		runner.setTaskSaver(saver);
		// load from MSP
		runner.load(null);
		
		assertEquals(1, runner.getTasks().size());
		
	}

}
