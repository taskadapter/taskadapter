package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.vaadin.ui.Notification;

import static com.taskadapter.web.ui.MessageUtils.nvl;

public class ShowProjectElement {

    private RedmineConfig config;

    public ShowProjectElement(RedmineConfig config) {
        this.config = config;
    }

    void showProjectInfo() throws BadConfigException {
        if (!config.getServerInfo().isHostSet()) {
            throw new ServerURLNotSetException();
        }
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
        notifyProjectLoaded(RedmineLoaders.loadProject(getRedmineManager(),
                config.getProjectKey()));
    }

    RedmineManager getRedmineManager() {
        RedmineManager mgr;
        final WebServerInfo serverInfo = config.getServerInfo();
        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            mgr = new RedmineManager(serverInfo.getHost(), serverInfo.getApiKey());
        } else {
            mgr = new RedmineManager(serverInfo.getHost());
            mgr.setLogin(serverInfo.getUserName());
            mgr.setPassword(serverInfo.getPassword());
        }
        return mgr;
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
