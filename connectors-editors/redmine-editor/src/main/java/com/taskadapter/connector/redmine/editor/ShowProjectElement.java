package com.taskadapter.connector.redmine.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.vaadin.ui.Notification;
import org.apache.http.client.HttpClient;

import static com.taskadapter.web.ui.MessageUtils.nvl;

public class ShowProjectElement {

    private RedmineConfig config;
    private WebConnectorSetup setup;
    // TODO TA3 reuse the same http client everywhere instead of creating it here
    private static final HttpClient httpClient = RedmineManagerFactory.createRedmineHttpClient();

    public ShowProjectElement(RedmineConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    void showProjectInfo() throws BadConfigException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
        final RedmineManager redmineManager = RedmineManagerFactory.createRedmineManager(setup, httpClient);
        final Project project = RedmineLoaders.loadProject(redmineManager.getProjectManager(),
                config.getProjectKey());
        notifyProjectLoaded(project);
    }

    private void notifyProjectLoaded(Project project) {
        String msg;
        if (project == null) {
            msg = "Project with the given key is not found";
        } else {
            msg = "Key:  " + project.getIdentifier()
                    + "\nName: " + project.getName()
                    + "\nCreated: " + project.getCreatedOn()
                    + "\nUpdated: " + project.getUpdatedOn()
                    + "\nHomepage: " + nvl(project.getHomepage())
                    + "\nDescription: " + nvl(project.getDescription());
        }
        Notification.show("Project Info", msg, Notification.Type.HUMANIZED_MESSAGE);
    }
}
