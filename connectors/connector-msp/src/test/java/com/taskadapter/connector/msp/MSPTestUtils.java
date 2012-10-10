package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;

import java.io.File;
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

    /**
     * Load the file with DEFAULT field mappings
     */
    static List<GTask> load(String fileNameInClasspath, Mappings mappings) throws Exception {
        String fileName = getTestFileAbsolutePath(fileNameInClasspath);
        MSPConfig config = new MSPConfig(fileName);
        final MSPConnector connector = new MSPConnector(config);
        return ConnectorUtils.loadDataOrderedById(connector, mappings);
    }

    /**
     * Load the file with DEFAULT field mappings into native task list
     */
    static List<Task> loadToMSPTaskList(String fileNameInClasspath) throws Exception {
        String fileName = getTestFileAbsolutePath(fileNameInClasspath);
        ProjectFile projectFile = new MSPFileReader().readFile(fileName);
        return projectFile.getAllTasks();
    }

    static boolean deleteFile(String fileName) {
        File f = new File(fileName);
        return f.delete();
    }
}
