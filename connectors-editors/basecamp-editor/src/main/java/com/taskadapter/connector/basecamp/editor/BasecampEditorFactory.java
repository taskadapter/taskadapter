package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;

import java.util.List;

public class BasecampEditorFactory implements PluginEditorFactory<BasecampConfig> {
    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, BasecampConfig config, List<BasecampConfig> relatedConfigs) {
        GridLayout gridLayout = new GridLayout(2, 4);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        int currentRow = 0;

        Label loginLabel = new Label("Login:");
        gridLayout.addComponent(loginLabel, 0, currentRow);
        TextField loginField = new TextField();
        loginField.setPropertyDataSource(new MethodProperty<String>(config.getAuth(), "login"));
        gridLayout.addComponent(loginField, 1, currentRow);

        currentRow++;

        Label passwordLabel = new Label("Password:");
        gridLayout.addComponent(passwordLabel, 0, currentRow);
        PasswordField passwordField = new PasswordField();
        passwordField.setPropertyDataSource(new MethodProperty<String>(config.getAuth(), "password"));
        gridLayout.addComponent(passwordField, 1, currentRow);

        currentRow++;

        Label accountIdLabel = new Label("Account Id:");
        gridLayout.addComponent(accountIdLabel, 0, currentRow);
        TextField accountIdField = new TextField();
        accountIdField.setPropertyDataSource(new MethodProperty<String>(config, "accountId"));
        gridLayout.addComponent(accountIdField, 1, currentRow);

        currentRow++;

        Label projectKeyLabel = new Label("Project key:");
        gridLayout.addComponent(projectKeyLabel, 0, currentRow);
        TextField projectKeyField = new TextField();
        projectKeyField.setPropertyDataSource(new MethodProperty<String>(config, "projectKey"));
        gridLayout.addComponent(projectKeyField, 1, currentRow);

        return gridLayout;
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
