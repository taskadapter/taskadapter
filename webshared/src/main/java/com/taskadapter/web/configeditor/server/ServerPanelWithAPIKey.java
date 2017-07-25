package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.web.configeditor.Validatable;
import com.taskadapter.webui.Page;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.Collection;

import static com.taskadapter.web.ui.Grids.*;
import static com.taskadapter.web.configeditor.EditorUtil.*;

public class ServerPanelWithAPIKey extends Panel implements Validatable {

    private TextField serverURL;
    private PasswordField apiKeyField;
    private TextField login;
    private PasswordField password;
    private OptionGroup authOptionsGroup;

    public ServerPanelWithAPIKey(String caption, Property<String> labelProperty,
            Property<String> serverURLProperty,
            Property<String> loginNameProperty,
            Property<String> passwordProperty, Property<String> apiKeyProperty,
            Property<Boolean> useApiKeyProperty) {
        setCaption(caption);
        buildUI(labelProperty, serverURLProperty, loginNameProperty, passwordProperty, apiKeyProperty, useApiKeyProperty);
        addListener();
        setAuthOptionsState((Boolean) authOptionsGroup.getValue());
    }

    private void buildUI(Property<String> labelProperty,
            Property<String> serverURLProperty,
            Property<String> loginNameProperty,
            Property<String> passwordProperty, Property<String> apiKeyProperty,
            Property<Boolean> useApiKeyProperty) {

        GridLayout layout = new GridLayout();
        setContent(layout);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setColumns(2);
        layout.setRows(8);

        int currentRow = 0;

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.name")));
        TextField labelField = textInput(labelProperty);
        labelField.addStyleName("server-panel-textfield");
        layout.addComponent(labelField, 1, currentRow);

        currentRow++;
        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.serverUrl")));
        serverURL = textInput(serverURLProperty);
        serverURL.addStyleName("server-panel-textfield");
        serverURL.setInputPrompt("http://myserver:3000/some_location");

        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, serverURL);

        String emptyLabelHeight = "10px";

        currentRow++;

        layout.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow++);

        Collection<Boolean> authOptions = new ArrayList<>();
        authOptions.add(true);
        authOptions.add(false);

        authOptionsGroup = new OptionGroup(Page.message("setupPanel.authorization"), authOptions);
        authOptionsGroup.setItemCaption(true, Page.message("setupPanel.useApiKey"));
        authOptionsGroup.setItemCaption(false, Page.message("setupPanel.useLogin"));
        authOptionsGroup.setPropertyDataSource(useApiKeyProperty);

        authOptionsGroup.setSizeFull();
        authOptionsGroup.setNullSelectionAllowed(false);
        authOptionsGroup.setImmediate(true);
        
        layout.addComponent(authOptionsGroup, 0, currentRow, 1, currentRow);
        layout.setComponentAlignment(authOptionsGroup, Alignment.MIDDLE_LEFT);

        currentRow++;
        layout.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow++);

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.apiAccessKey")));

        apiKeyField = new PasswordField();
        apiKeyField.setPropertyDataSource(apiKeyProperty);
        apiKeyField.addStyleName("server-panel-textfield");
        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, apiKeyField);
        currentRow++;

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.login")));

        login = textInput(loginNameProperty);
        login.addStyleName("server-panel-textfield");
        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, login);
        currentRow++;

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.password")));

        password = new PasswordField();
        password.addStyleName("server-panel-textfield");
        password.setPropertyDataSource(passwordProperty);
        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, password);
    }

    private Label createEmptyLabel(String height) {
        Label label = new Label("&nbsp;", ContentMode.HTML);
        label.setHeight(height);
        return label;
    }

    private void addListener() {
        authOptionsGroup.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean useAPIOptionSelected = (Boolean) authOptionsGroup.getValue();
                setAuthOptionsState(useAPIOptionSelected);
            }
        });
    }

    private void setAuthOptionsState(boolean useAPIKey) {
        apiKeyField.setEnabled(useAPIKey);
        login.setEnabled(!useAPIKey);
        password.setEnabled(!useAPIKey);
    }

    @Override
    public void validate() throws ServerURLNotSetException {
        String host = serverURL.getValue();
        if (host == null || host.isEmpty() || host.equalsIgnoreCase(WebServerInfo.DEFAULT_URL_PREFIX)) {
            throw new ServerURLNotSetException();
        }
    }
}
