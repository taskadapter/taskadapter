package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.vaadin.ui.Notification;

import static com.taskadapter.web.ui.MessageUtils.nvl;

public class ShowProjectElement {

    private RedmineConfig config;
    private WebServerInfo webServerInfo;

    public ShowProjectElement(RedmineConfig config, WebServerInfo webServerInfo) {
        this.config = config;
        this.webServerInfo = webServerInfo;
    }

    void showProjectInfo() throws BadConfigException {
        if (!webServerInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
        final RedmineManager redmineManager = RedmineManagerFactory.createRedmineManager(webServerInfo);
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
