package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.server.ServerPanelWithAPIKey;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;

import java.util.List;

public class BasecampEditorFactory implements PluginEditorFactory<BasecampConfig> {
    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, BasecampConfig config, List<BasecampConfig> relatedConfigs) {

        Panel panel = new Panel("Server Info");
        ServerPanelWithAPIKey redmineServerPanel = new ServerPanelWithAPIKey(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(config, "serverUrl"),
                new MethodProperty<String>(config.getAuth(), "login"),
                new MethodProperty<String>(config.getAuth(), "password"),
                new MethodProperty<String>(config.getAuth(), "apiKey"),
                new MethodProperty<Boolean>(config.getAuth(), "useAPIKeyInsteadOfLoginPassword"));
        panel.addComponent(redmineServerPanel);

        GridLayout grid = new GridLayout();
        grid.setColumns(2);
        grid.setMargin(true);
        grid.setSpacing(true);

        grid.addComponent(panel);

        Panel projectPanel = new Panel("Project");

        GridLayout projectGrid = new GridLayout();
        projectGrid.setColumns(2);
        projectGrid.setMargin(true);
        projectGrid.setSpacing(true);

        projectPanel.addComponent(projectGrid);

        grid.addComponent(projectPanel);

        Label accountIdLabel = new Label("Account Id:");
        projectGrid.addComponent(accountIdLabel);
        TextField accountIdField = new TextField();
        accountIdField.setPropertyDataSource(new MethodProperty<String>(config, "accountId"));
        projectGrid.addComponent(accountIdField);

        Label projectKeyLabel = new Label("Project key:");
        projectGrid.addComponent(projectKeyLabel);
        TextField projectKeyField = new TextField();
        projectKeyField.setPropertyDataSource(new MethodProperty<String>(config, "projectKey"));
        projectGrid.addComponent(projectKeyField);

        Label todoListKey = new Label("Todo list key:");
        projectGrid.addComponent(todoListKey);
        TextField todoListField = new TextField();
        todoListField.setPropertyDataSource(new MethodProperty<String>(config, "todoKey"));
        projectGrid.addComponent(todoListField);

        return grid;
    }

    @Override
    public void validateForSave(BasecampConfig config) throws BadConfigException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void validateForLoad(BasecampConfig config) throws BadConfigException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String describeSourceLocation(BasecampConfig config) {
        return "basecamp.com";
    }

    @Override
    public String describeDestinationLocation(BasecampConfig config) {
        return "basecamp.com";
    }

    @Override
    public String formatError(Throwable e) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
