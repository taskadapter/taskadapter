package com.taskadapter.connector.it;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class RedmineTestInitializer {
    private static final Logger logger = LoggerFactory.getLogger(RedmineTestInitializer.class);

    private static WebConnectorSetup setup = RedmineTestConfig.getRedmineServerInfo();
    public static RedmineManager mgr = RedmineManagerFactory.createRedmineManager(setup);

    public static Project createProject() {
        logger.info("Running Redmine tests with: " + setup);

        Project project = ProjectFactory.create("integration tests",
                "itest" + Calendar.getInstance().getTimeInMillis());
        try {
            Project redmineProject = mgr.getProjectManager().createProject(project);
            logger.info("Created temporary Redmine project with key " + redmineProject.getIdentifier());
            return redmineProject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteProject(String projectKey) {
        try {
            mgr.getProjectManager().deleteProject(projectKey);
            logger.info("Deleted temporary Redmine project with ID " + projectKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
