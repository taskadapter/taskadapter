package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.ResourceLoader;
import com.taskadapter.model.GTask;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;

import java.util.List;

public class MSPTestUtils {

    private static final String FILE_TO_READ = "project_test_data.xml";
    private static MSPXMLFileReader fileReader = new MSPXMLFileReader();

    public static ProjectFile readTestProjectFile() {
        try {
            String path = ResourceLoader.getAbsolutePathForResource(FILE_TO_READ);
            return fileReader.readFile(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        String fileName = ResourceLoader.getAbsolutePathForResource(fileNameInClasspath);
        MSPConfig config = new MSPConfig(fileName);
        final MSPConnector connector = new MSPConnector(config);
        return ConnectorUtils.loadDataOrderedById(connector);
    }

    /**
     * Load the file into native task list
     */
    public static List<Task> loadToMSPTaskList(String fileNameInClasspath) {
        String fileName = ResourceLoader.getAbsolutePathForResource(fileNameInClasspath);
        try {
            ProjectFile projectFile = new MSPFileReader().readFile(fileName);
            return projectFile.getAllTasks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
