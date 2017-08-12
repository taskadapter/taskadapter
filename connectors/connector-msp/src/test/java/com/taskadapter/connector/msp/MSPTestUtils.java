package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;

import java.net.URL;
import java.util.List;

public class MSPTestUtils {

    private static final String FILE_TO_READ = "project_test_data.xml";
    private static MSPXMLFileReader fileReader = new MSPXMLFileReader();

    public static ProjectFile readTestProjectFile() {
        try {
            String path = getTestFileAbsolutePath();
            return fileReader.readFile(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getTestFileAbsolutePath() {
        return getTestFileAbsolutePath(FILE_TO_READ);
    }

    private static String getTestFileAbsolutePath(String name) {
        URL url = MSPTestUtils.class.getClassLoader().getResource(name);
        String path = null;
        try {
            if (url.getProtocol().startsWith("bundleresource")) {
                // for running inside OSGI via Maven
//        		URL nativeURL = FileLocator.resolve(url);
//                path = nativeURL.toURI().getPath();
                throw new RuntimeException("not implemented");
            } else {
                // for running tests in IDE
                path = url.toURI().getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    static Task findMSPTaskBySummary(List<Task> tasks, String summary) {
        for (Task t : tasks) {
            if (t.getName().equals(summary)) {
                return t;
            }
        }
        return null;
    }

    static List<GTask> load(String fileNameInClasspath) throws ConnectorException {
        String fileName = getTestFileAbsolutePath(fileNameInClasspath);
        MSPConfig config = new MSPConfig(fileName);
        final MSPConnector connector = new MSPConnector(config);
        return ConnectorUtils.loadDataOrderedById(connector);
    }

    /**
     * Load the file into native task list
     */
    public static List<Task> loadToMSPTaskList(String fileNameInClasspath) {
        String fileName = getTestFileAbsolutePath(fileNameInClasspath);
        try {
            ProjectFile projectFile = new MSPFileReader().readFile(fileName);
            return projectFile.getAllTasks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
