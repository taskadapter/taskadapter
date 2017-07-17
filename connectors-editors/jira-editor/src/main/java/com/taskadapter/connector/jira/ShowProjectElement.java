package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.GProject;
import com.vaadin.ui.Notification;

import static com.taskadapter.web.ui.MessageUtils.nvl;

// TODO unify with the same class in Redmine editor module?
public class ShowProjectElement {
    private JiraConfig jiraConfig;
    private WebServerInfo webServerInfo;

    public ShowProjectElement(JiraConfig jiraConfig, WebServerInfo webServerInfo) {
        this.jiraConfig = jiraConfig;
        this.webServerInfo = webServerInfo;
    }

    /**
     * Load and show the project info.
     */
    void loadProjectInfo() throws ConnectorException {
        if (!webServerInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }
        if (jiraConfig.getProjectKey() == null || jiraConfig.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
        GProject project = JiraLoaders.loadProject(webServerInfo, jiraConfig.getProjectKey());
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
