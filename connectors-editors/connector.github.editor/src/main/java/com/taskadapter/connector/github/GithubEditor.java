package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class GithubEditor extends ConfigEditor {

    private TextField userNameText;

    private PasswordField passwordText;

    private TextField projectKey;

    public GithubEditor(ConnectorConfig config) {
        super(config);
        buildUI();
        addFieldsMappingPanel(GithubDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());

        setGitHubData();
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        GithubConfig newConfig = new GithubConfig();

        WebServerInfo serverInfo = new WebServerInfo(null, (String) userNameText.getValue(), (String) passwordText.getValue());
        newConfig.setServerInfo(serverInfo);

        newConfig.setProjectKey((String) projectKey.getValue());

        return newConfig;
    }

    private void buildUI() {

        userNameText = new TextField("Login:");
        addComponent(userNameText);
        passwordText = new PasswordField("Password:");
        addComponent(passwordText);
        projectKey = new TextField("Project key:");
        addComponent(projectKey);
    }

    private void setGitHubData() {
        GithubConfig githubConfig = (GithubConfig) config;
        WebServerInfo serverInfo = githubConfig.getServerInfo();
        setIfNotNull(userNameText, serverInfo.getUserName());
        setIfNotNull(passwordText, serverInfo.getPassword());
        setIfNotNull(projectKey, githubConfig.getProjectKey());
    }
}
