package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.Validatable;
import com.vaadin.data.Property;
import com.vaadin.ui.*;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Project;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class RedmineEditor extends ConfigEditor implements LoadProjectJobResultListener {

    private RedmineServerPanel serverPanel;
    private OtherRedmineFieldsPanel otherPanel;

    public RedmineEditor(ConnectorConfig config) {
        super(config);

        buildUI();
        setData(config);
    }

    private void buildUI() {
        serverPanel = new RedmineServerPanel();
        addCustomPanelToProjectServerPanel(serverPanel);

        addProjectPanel(this, new RedmineProjectProcessor(this));

        otherPanel = new OtherRedmineFieldsPanel(this);
        addComponent(otherPanel);

        addFieldsMappingPanel(RedmineDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        RedmineConfig rmConfig = new RedmineConfig();
        WebServerInfo serverInfo = new WebServerInfo(serverPanel.getServerURL(), serverPanel.getLogin(),
                serverPanel.getPassword());
        serverInfo.setApiKey(serverPanel.getRedmineAPIKey());
        serverInfo.setUseAPIKeyInsteadOfLoginPassword(serverPanel.getAuthOption());
        rmConfig.setServerInfo(serverInfo);

        rmConfig.setDefaultTaskType(otherPanel.getDefaultTaskType());
        rmConfig.setSaveIssueRelations(otherPanel.getSaveRelation());
        return rmConfig;
    }

    RedmineManager getRedmineManager() {
        RedmineManager mgr;
        if (serverPanel.getAuthOption()) {
            mgr = new RedmineManager(serverPanel.getServerURL(), serverPanel.getRedmineAPIKey());
        } else {
            mgr = new RedmineManager(serverPanel.getServerURL());
            mgr.setLogin(serverPanel.getLogin());
            mgr.setPassword(serverPanel.getPassword());
        }
        return mgr;
    }

    @Override
    public void notifyProjectLoaded(Project project) {
        String msg;
        if (project == null) {
            msg = "<br>Project with the given key is not found";
        } else {
            msg = "<br>Key:  " + project.getIdentifier()
                    + "<br>Name: " + project.getName()
                    + "<br>Created: " + project.getCreatedOn()
                    + "<br>Updated: " + project.getUpdatedOn();
            msg += addNullSafe("<br>Homepage", project.getHomepage());
            msg += addNullSafe("<br>Description", project.getDescription());
        }
        EditorUtil.show(getWindow(), "Project Info", msg);
    }

    private String addNullSafe(String label, String fieldValue) {
        String msg = "\n" + label + ": ";
        if (fieldValue != null) {
            msg += fieldValue;
        }
        return msg;
    }

    class RedmineServerPanel extends GridLayout implements Validatable {
        private static final String SERVER_INFO_LABEL = "Redmine Server Info";
        private static final String USE_API = "Use API Access Key";
        private static final String USE_LOGIN = "Use Login and Password";
        private static final String DEFAULT_USE = USE_LOGIN;

        private TextField serverURL;
        private PasswordField redmineAPIKey;
        private TextField login;
        private PasswordField password;

        private final List<String> authOptions = Arrays.asList(USE_API, USE_LOGIN);
        private OptionGroup authOptionsGroup = new OptionGroup("Authorization", authOptions);


        public RedmineServerPanel() {
            buildUI();
            setDataToForm();
        }

        private void buildUI() {
            addStyleName("bordered-panel");
            setCaption(SERVER_INFO_LABEL);
            setSpacing(true);
            setMargin(true);
            setColumns(2);
            setRows(5);

            Label urlLabel = new Label("Redmine URL:");
            addComponent(urlLabel, 0, 0);
            setComponentAlignment(urlLabel, Alignment.MIDDLE_LEFT);

            serverURL = new TextField();
            serverURL.addStyleName("server-panel-textfield");
            serverURL.setInputPrompt("http://myserver:3000/myredminelocation");
            addComponent(serverURL, 1, 0);
            setComponentAlignment(serverURL, Alignment.MIDDLE_LEFT);

            authOptionsGroup.setSizeFull();
            authOptionsGroup.setNullSelectionAllowed(false);
            authOptionsGroup.setImmediate(true);
            authOptionsGroup.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    System.out.println("TODO: disable fields on the page (e.g. disable user name if API Auth method is used)");
                }
            });
            authOptionsGroup.select(DEFAULT_USE);
            addComponent(authOptionsGroup, 0, 1, 1, 1);
            setComponentAlignment(authOptionsGroup, Alignment.MIDDLE_LEFT);

            Label apiKeyLabel = new Label("API access key:");
            addComponent(apiKeyLabel, 0, 2);
            setComponentAlignment(apiKeyLabel, Alignment.MIDDLE_LEFT);

            redmineAPIKey = new PasswordField();
            redmineAPIKey.addStyleName("server-panel-textfield");
            addComponent(redmineAPIKey, 1, 2);
            setComponentAlignment(redmineAPIKey, Alignment.MIDDLE_LEFT);

            Label loginLabel = new Label("Login:");
            addComponent(loginLabel, 0, 3);
            setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

            login = new TextField();
            login.addStyleName("server-panel-textfield");
            addComponent(login, 1, 3);
            setComponentAlignment(login, Alignment.MIDDLE_LEFT);

            Label passwordLabel = new Label("Password:");
            addComponent(passwordLabel, 0, 4);
            setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

            password = new PasswordField();
            password.addStyleName("server-panel-textfield");
            addComponent(password, 1, 4);
            setComponentAlignment(password, Alignment.MIDDLE_LEFT);
        }

        private void setAuthOptionsState(boolean useAPIKey) {
            redmineAPIKey.setEnabled(useAPIKey);
            login.setEnabled(!useAPIKey);
            password.setEnabled(!useAPIKey);
        }

        private void setDataToForm() {
            RedmineConfig redmineConfig = (RedmineConfig) config;

            serverURL.setValue(redmineConfig.getServerInfo().getHost());
            WebServerInfo serverInfo = redmineConfig.getServerInfo();
            setIfNotNull(serverURL, serverInfo.getHost());
            setIfNotNull(redmineAPIKey, serverInfo.getApiKey());
            setIfNotNull(login, serverInfo.getUserName());
            setIfNotNull(password, serverInfo.getPassword());
            authOptionsGroup.select(serverInfo.isUseAPIKeyInsteadOfLoginPassword());
            authOptionsGroup.select(!serverInfo.isUseAPIKeyInsteadOfLoginPassword());
            setAuthOptionsState(serverInfo.isUseAPIKeyInsteadOfLoginPassword());
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

        public boolean getAuthOption() {
            return authOptionsGroup.getValue().equals(USE_API);
        }

        @Override
        public void validate() throws ValidationException {
            if (getServerURL().isEmpty()) {
                throw new ValidationException("'Server URL' is not set");
            }
        }
    }

    class OtherRedmineFieldsPanel extends VerticalLayout {
        private static final String OTHER_PANEL_LABEL = "Additional Info";
        private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

        private ConfigEditor configEditor;

        private TextField defaultTaskType;
        private CheckBox saveRelations;

        public OtherRedmineFieldsPanel(ConfigEditor configEditor) {
            this.configEditor = configEditor;

            buildUI();
            setDataToForm();
        }

        private void buildUI() {
            addStyleName("bordered-panel");
            setWidth("350px");
            setCaption(OTHER_PANEL_LABEL);
            setSpacing(true);
            setMargin(true);

            HorizontalLayout taskTypeLayout = new HorizontalLayout();
            taskTypeLayout.addStyleName("bordered-panel");
            taskTypeLayout.setSizeUndefined();
            taskTypeLayout.setSpacing(true);

            defaultTaskType = EditorUtil.addLabeledText(taskTypeLayout, "Default task type:",
                    "New tasks will be created with this 'tracker' (bug/task/support/feature/...)");

            Button showDefaultTaskType = EditorUtil.createLookupButton(
                    configEditor,
                    "...",
                    "Show list of available tracker types on the Redmine server",
                    "Select task type",
                    "List of available task types on the Redmine server",
                    new LoadTrackersOperation(configEditor, new RedmineFactory()),
                    defaultTaskType,
                    true
            );
            taskTypeLayout.addComponent(showDefaultTaskType);
            addComponent(taskTypeLayout);

            addComponent(createFindUsersElementIfNeeded());

            saveRelations = new CheckBox(SAVE_ISSUE_LABEL);
            addComponent(saveRelations);

        }

        private void setDataToForm() {
            RedmineConfig redmineConfig = (RedmineConfig) config;
            setIfNotNull(defaultTaskType, redmineConfig.getDefaultTaskType());
            setIfNotNull(saveRelations, redmineConfig.getSaveIssueRelations());
        }

        public String getDefaultTaskType() {
            return (String) defaultTaskType.getValue();
        }

        public Boolean getSaveRelation() {
            return (Boolean) saveRelations.getValue();
        }
    }
}
