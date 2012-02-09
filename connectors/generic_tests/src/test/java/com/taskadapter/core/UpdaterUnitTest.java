package com.taskadapter.core;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class UpdaterUnitTest {

    private MSPConnector projectConnector;

    @Before
    public void init() {
        String testFileLocation = getFileAbsolutePath("com/taskadapter/core/9tasks.xml");
        MSPConfig mspConfig = new MSPConfig(testFileLocation);
        projectConnector = new MSPConnector(mspConfig);
    }

    @Test
    public void tasksWithoutRemoteIdsAreFiltered() {
        Updater updater = new Updater(projectConnector, null);
        updater.loadTasksFromFile(null);
        assertEquals(9, updater.getExistingTasks().size());
        updater.removeTasksWithoutRemoteIds();
        // only 7 tasks have remote IDs
        assertEquals(7, updater.getExistingTasks().size());
    }

    public String getFileAbsolutePath(String name) {
        URL url = this.getClass().getClassLoader().getResource(name);
        String path = null;
        try {
            if (url.getProtocol().startsWith("bundleresource")) {
                // for running inside OSGI via Maven
//				URL nativeURL = FileLocator.resolve(url);
//				path = nativeURL.toURI().getPath();
                throw new RuntimeException("OSGI : not implemented");
            } else {
                // for running tests in IDE
                path = url.toURI().getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

}
