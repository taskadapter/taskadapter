package com.taskadapter.integrationtests;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.connector.redmine.RedmineToGUser;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.internal.Transport;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class RedmineTestInitializer {
    private static Logger logger = LoggerFactory.getLogger(RedmineTestInitializer.class);
    private static WebConnectorSetup setup = TestConfigs.getRedmineSetup();

    // TODO TA3 reuse the same http client everywhere instead of creating it here
    private static HttpClient httpClient = RedmineManagerFactory.createRedmineHttpClient(setup.getHost());

    public static RedmineManager mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient);

    private static final User redmineUser;

    public static final GUser currentUser;

    private static final Transport transport;

    static {
        try {
            redmineUser = mgr.getUserManager().getCurrentUser();
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        currentUser = RedmineToGUser.convertToGUser(redmineUser);
        transport = mgr.getTransport();
    }

    public static Project createProject() {
        logger.info("Running Redmine tests with: " + setup);
        try {
            Project project = new Project(transport, "integration tests", "itest" + Calendar.getInstance().getTimeInMillis())
                    .create();
            logger.info("Created temporary Redmine project with key " + project.getIdentifier());
            return project;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteProject(String projectKey) throws RedmineException {
        try {
            mgr.getProjectManager().deleteProject(projectKey);
            logger.info("Deleted temporary Redmine project with ID " + projectKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
