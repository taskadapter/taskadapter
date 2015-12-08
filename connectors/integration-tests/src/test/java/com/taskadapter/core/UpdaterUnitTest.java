package com.taskadapter.core;

import com.google.common.io.Resources;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPSupportedFields;
import com.taskadapter.connector.redmine.RedmineSupportedFields;
import com.taskadapter.connector.testlib.TestMappingUtils;

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

    @Test
    public void tasksWithoutRemoteIdsAreFiltered() throws ConnectorException {
        // TODO refactor: DefaultRedmineMappings.generate() is not even used in Updater in this case.
        Updater updater = new Updater(projectConnector,
                TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS), null,
                TestMappingUtils.fromFields(RedmineSupportedFields.SUPPORTED_FIELDS), "someTestData");
        updater.loadTasksFromFile(ProgressMonitorUtils.DUMMY_MONITOR);
        assertEquals(9, updater.getExistingTasks().size());
        updater.removeTasksWithoutRemoteIds();
        // only 7 tasks have remote IDs
        assertEquals(7, updater.getExistingTasks().size());
    }
}
