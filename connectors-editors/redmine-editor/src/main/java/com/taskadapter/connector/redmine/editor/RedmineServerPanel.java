package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.Validatable;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.util.Arrays;
import java.util.List;

class RedmineServerPanel extends VerticalLayout implements Validatable {
    private static final String USE_API = "Use API Access Key";
    private static final String USE_LOGIN = "Use Login and Password";
    private static final String DEFAULT_USE = USE_LOGIN;

    private TextField descriptionField;
    private TextField serverURL;
    private PasswordField redmineAPIKey;
    private TextField login;
    private PasswordField password;

    private final List<String> authOptions = Arrays.asList(USE_API, USE_LOGIN);
    private OptionGroup authOptionsGroup = new OptionGroup("Authorization", authOptions);
    private WindowProvider windowProvider;
    private RedmineConfig config;

    public RedmineServerPanel(final WindowProvider windowProvider, RedmineConfig config) {
        this.windowProvider = windowProvider;
        this.config = config;
        buildUI();
        addListener();
    }

    private void buildUI() {
//        setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        // set spacing around the layout (not between components in the layout!)
//        setMargin(true);

        GridLayout layout = new GridLayout();
        addComponent(layout);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setColumns(2);
        layout.setRows(8);

        int currentRow = 0;

        Label descriptionLabel = new Label("Description:");
        layout.addComponent(descriptionLabel, 0, currentRow);
        layout.setComponentAlignment(descriptionLabel, Alignment.MIDDLE_LEFT);
        descriptionField = new TextField();
        descriptionField.addStyleName("server-panel-textfield");
        descriptionField.setPropertyDataSource(new MethodProperty<String>(config, "label"));
        layout.addComponent(descriptionField, 1, currentRow);

        currentRow++;
        Label urlLabel = new Label("Server URL:");
        layout.addComponent(urlLabel, 0, currentRow);
        layout.setComponentAlignment(urlLabel, Alignment.MIDDLE_LEFT);
        serverURL = new TextField();
        serverURL.addStyleName("server-panel-textfield");
        serverURL.setInputPrompt("http://myserver:3000/myredminelocation");
        serverURL.addListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent event) {
                //TODO refactor these methods (common in ServerPanel and RedmineServerPanel
                checkProtocol();
            }
        });
        WebServerInfo serverInfo = config.getServerInfo();
        serverURL.setPropertyDataSource(new MethodProperty<String>(serverInfo, "host"));

        layout.addComponent(serverURL, 1, currentRow);

        String emptyLabelHeight = "10px";

        currentRow++;

        layout.setComponentAlignment(serverURL, Alignment.MIDDLE_LEFT);
        layout.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow++);
        authOptionsGroup.setSizeFull();
        authOptionsGroup.setNullSelectionAllowed(false);
        authOptionsGroup.setImmediate(true);
        authOptionsGroup.select(DEFAULT_USE);
        layout.addComponent(authOptionsGroup, 0, currentRow, 1, currentRow);
        layout.setComponentAlignment(authOptionsGroup, Alignment.MIDDLE_LEFT);

        currentRow++;
        layout.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow++);

        Label apiKeyLabel = new Label("API access key:");
        layout.addComponent(apiKeyLabel, 0, currentRow);
        layout.setComponentAlignment(apiKeyLabel, Alignment.MIDDLE_LEFT);

        redmineAPIKey = new PasswordField();
        redmineAPIKey.addStyleName("server-panel-textfield");
        layout.addComponent(redmineAPIKey, 1, currentRow);
        layout.setComponentAlignment(redmineAPIKey, Alignment.MIDDLE_LEFT);
        currentRow++;
        redmineAPIKey.setPropertyDataSource(new MethodProperty<String>(serverInfo, "apiKey"));

        Label loginLabel = new Label("Login:");
        layout.addComponent(loginLabel, 0, currentRow);
        layout.setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        login = new TextField();
        login.addStyleName("server-panel-textfield");
        login.setPropertyDataSource(new MethodProperty<String>(serverInfo, "userName"));
        layout.addComponent(login, 1, currentRow);
        layout.setComponentAlignment(login, Alignment.MIDDLE_LEFT);
        currentRow++;

        Label passwordLabel = new Label("Password:");
        layout.addComponent(passwordLabel, 0, currentRow);
        layout.setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        password = new PasswordField();
        password.addStyleName("server-panel-textfield");
        password.setPropertyDataSource(new MethodProperty<String>(serverInfo, "password"));
        layout.addComponent(password, 1, currentRow);
        layout.setComponentAlignment(password, Alignment.MIDDLE_LEFT);

        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            authOptionsGroup.select(USE_API);
        } else {
            authOptionsGroup.select(USE_LOGIN);
        }
        setAuthOptionsState(serverInfo.isUseAPIKeyInsteadOfLoginPassword());
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
                final boolean useAPIOptionSelected = isUseAPIOptionSelected();
                setAuthOptionsState(useAPIOptionSelected);
                config.getServerInfo().setUseAPIKeyInsteadOfLoginPassword(useAPIOptionSelected);
            }
        });
    }

    private void setAuthOptionsState(boolean useAPIKey) {
        redmineAPIKey.setEnabled(useAPIKey);
        login.setEnabled(!useAPIKey);
        password.setEnabled(!useAPIKey);
    }

    public String getServerURL() {
        return (String) serverURL.getValue();
    }

    public String getRedmineAPIKey() {
        return (String) redmineAPIKey.getValue();
    }

    public String getLogin() {
        return (String) login.getValue();
    }

    public String getPassword() {
        return (String) password.getValue();
    }

    public boolean isUseAPIOptionSelected() {
        return authOptionsGroup.getValue().equals(USE_API);
    }

    @Override
    public void validate() throws ValidationException {
        String host = getServerURL();
        if (host == null || host.isEmpty() || host.equalsIgnoreCase(WebServerInfo.DEFAULT_URL_PREFIX)) {
            throw new ValidationException("Redmine server URL is not set");
        }
    }

    private void checkProtocol() {
        String serverURLValue = (String) serverURL.getValue();
        if (!serverURLValue.trim().isEmpty() && !serverURLValue.startsWith("http")) {
            serverURL.setValue(WebServerInfo.DEFAULT_URL_PREFIX + serverURLValue);
        }
    }
}
