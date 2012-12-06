package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.web.configeditor.Validatable;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.Collection;

import static com.taskadapter.web.ui.Grids.*;
import static com.taskadapter.web.configeditor.EditorUtil.*;

public class ServerPanelWithAPIKey extends VerticalLayout implements Validatable {
    private static final String USE_API = "Use API Access Key";
    private static final String USE_LOGIN = "Use Login and Password";

    private TextField serverURL;
    private PasswordField apiKeyField;
    private TextField login;
    private PasswordField password;
    private OptionGroup authOptionsGroup;

    public ServerPanelWithAPIKey(Property labelProperty, Property serverURLProperty, Property loginNameProperty,
                                 Property passwordProperty, Property apiKeyProperty, Property useApiKeyProperty) {
        buildUI(labelProperty, serverURLProperty, loginNameProperty, passwordProperty, apiKeyProperty, useApiKeyProperty);
        addListener();
        setAuthOptionsState((Boolean) authOptionsGroup.getValue());
    }

    private void buildUI(Property labelProperty, Property serverURLProperty,
                         Property loginNameProperty,
                         Property passwordProperty, Property apiKeyProperty, Property useApiKeyProperty) {

        GridLayout layout = new GridLayout();
        addComponent(layout);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setColumns(2);
        layout.setRows(8);

        int currentRow = 0;

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label("Description:"));
        TextField descriptionField = textInput(labelProperty);
        descriptionField.addStyleName("server-panel-textfield");
        layout.addComponent(descriptionField, 1, currentRow);

        currentRow++;
        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label("Server URL:"));
        serverURL = textInput(serverURLProperty);
        serverURL.addStyleName("server-panel-textfield");
        serverURL.setInputPrompt("http://myserver:3000/some_location");

        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, serverURL);

        String emptyLabelHeight = "10px";

        currentRow++;

        layout.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow++);

        Collection<Boolean> authOptions = new ArrayList<Boolean>();
        authOptions.add(true);
        authOptions.add(false);

        authOptionsGroup = new OptionGroup("Authorization", authOptions);
        authOptionsGroup.setItemCaption(true, USE_API);
        authOptionsGroup.setItemCaption(false, USE_LOGIN);
        authOptionsGroup.setPropertyDataSource(useApiKeyProperty);

        authOptionsGroup.setSizeFull();
        authOptionsGroup.setNullSelectionAllowed(false);
        authOptionsGroup.setImmediate(true);
        
        layout.addComponent(authOptionsGroup, 0, currentRow, 1, currentRow);
        layout.setComponentAlignment(authOptionsGroup, Alignment.MIDDLE_LEFT);

        currentRow++;
        layout.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow++);

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label("API access key:"));

        apiKeyField = new PasswordField();
        apiKeyField.setPropertyDataSource(apiKeyProperty);
        apiKeyField.addStyleName("server-panel-textfield");
        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, apiKeyField);
        currentRow++;

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label("Login:"));

        login = textInput(loginNameProperty);
        login.addStyleName("server-panel-textfield");
        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, login);
        currentRow++;

        addTo(layout, 0, currentRow, Alignment.MIDDLE_LEFT, new Label("Password:"));

        password = new PasswordField();
        password.addStyleName("server-panel-textfield");
        password.setPropertyDataSource(passwordProperty);
        addTo(layout, 1, currentRow, Alignment.MIDDLE_LEFT, password);
    }

    private Label createEmptyLabel(String height) {
        Label label = new Label("&nbsp;", Label.CONTENT_XHTML);
        label.setHeight(height);
        return label;
    }

    private void addListener() {
        authOptionsGroup.addListener(new Property.ValueChangeListener() {
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
        String host = (String) serverURL.getValue();
        if (host == null || host.isEmpty() || host.equalsIgnoreCase(WebServerInfo.DEFAULT_URL_PREFIX)) {
            throw new ServerURLNotSetException();
        }
    }
}
