package com.taskadapter.core;

import com.google.common.io.Resources;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class UpdaterUnitTest {

    private MSPConnector projectConnector;

    @Before
    public void beforeEachTest() throws URISyntaxException {
        URL resource = Resources.getResource("com/taskadapter/core/9tasks.xml");
        MSPConfig mspConfig = new MSPConfig(resource.toURI().getPath());
        projectConnector = new MSPConnector(mspConfig);
    }

	// TODO !!! fix tests
    /*
     
     @Test
    public void tasksWithoutRemoteIdsAreFiltered() throws ConnectorException {
        Updater updater = new Updater(projectConnector, null);
        updater.loadTasksFromFile(ProgressMonitorUtils.getDummyMonitor());
        assertEquals(9, updater.getExistingTasks().size());
        updater.removeTasksWithoutRemoteIds();
        // only 7 tasks have remote IDs
        assertEquals(7, updater.getExistingTasks().size());
    }*/
}
