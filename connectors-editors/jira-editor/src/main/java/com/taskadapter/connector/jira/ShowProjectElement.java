package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.GProject;
import com.vaadin.ui.Notification;

import static com.taskadapter.web.ui.MessageUtils.nvl;

// TODO unify with the same class in Redmine editor module?
public class ShowProjectElement {
    private JiraConfig jiraConfig;
    private WebConnectorSetup setup;

    public ShowProjectElement(JiraConfig jiraConfig, WebConnectorSetup webServerInfo) {
        this.jiraConfig = jiraConfig;
        this.setup = webServerInfo;
    }

    /**
     * Load and show the project info.
     */
    void loadProjectInfo() throws ConnectorException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
        if (jiraConfig.getProjectKey() == null || jiraConfig.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
        GProject project = JiraLoaders.loadProject(setup, jiraConfig.getProjectKey());
        showProjectInfo(project);
    }

    private void showProjectInfo(GProject project) {
        String msg = "Key:  " + project.getKey()
                + "\nName: " + project.getName()
                + "\nHomepage: " + nvl(project.homepage())
                + "\nDescription: " + nvl(project.description());
        Notification.show("Project Info", msg, Notification.Type.HUMANIZED_MESSAGE);
    }

}
