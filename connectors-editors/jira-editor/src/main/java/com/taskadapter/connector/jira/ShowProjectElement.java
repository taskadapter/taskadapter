package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.EditorUtil;

// TODO unify with the same class in Redmine editor module?
public class ShowProjectElement {
    private WindowProvider windowProvider;
    private JiraConfig jiraConfig;

    public ShowProjectElement(WindowProvider windowProvider, JiraConfig jiraConfig) {
        this.windowProvider = windowProvider;
        this.jiraConfig = jiraConfig;
    }

    /**
     * Shows a project info.
     *
     * @throws com.taskadapter.connector.definition.ValidationException
     * @throws com.taskadapter.connector.definition.exceptions.ConnectorException
     */
    void loadProjectInfo() throws ValidationException, ConnectorException {
        if (!jiraConfig.getServerInfo().isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }
        if (jiraConfig.getProjectKey() == null || jiraConfig.getProjectKey().isEmpty()) {
            throw new ValidationException("Please, provide the project key first");
        }
        GProject project = JiraLoaders.loadProject(
                jiraConfig.getServerInfo(), jiraConfig.getProjectKey());
        showProjectInfo(project);
    }

    private void showProjectInfo(GProject project) {
        String msg = "Id: " + project.getId() + "\nKey:  "
                + project.getKey() + "\nName: "
                + project.getName();
        // + "\nLead: " + project.getLead()
        // + "\nURL: " + project.getProjectUrl()
        msg += addNullSafe("Homepage", project.getHomepage());
        msg += addNullSafe("Description", project.getDescription());

        EditorUtil.show(windowProvider.getWindow(), "Project Info", msg);
    }

    private String addNullSafe(String label, String fieldValue) {
        String msg = "\n" + label + ": ";
        if (fieldValue != null) {
            msg += fieldValue;
        }
        return msg;
    }

}
