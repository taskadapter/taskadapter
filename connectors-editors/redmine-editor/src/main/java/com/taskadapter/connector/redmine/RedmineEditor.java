package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.*;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.*;

import java.util.Arrays;
import java.util.List;

public class RedmineEditor extends TwoColumnsConfigEditor {

    public RedmineEditor(ConnectorConfig config, Services services) {
        super(config, services);

        buildUI();
    }

    @SuppressWarnings("unchecked")
	private void buildUI() {

        RedmineServerPanel serverPanel = new RedmineServerPanel();
        addToLeftColumn(serverPanel);

        addToLeftColumn(createEmptyLabel("15px"));

        OtherRedmineFieldsPanel otherPanel = new OtherRedmineFieldsPanel(this, (RedmineConfig) config);
        addToLeftColumn(otherPanel);

		addToRightColumn(new ProjectPanel(this,
				EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
				EditorUtil.wrapNulls(new MethodProperty<Integer>(config, "queryId")),
				Interfaces.fromMethod(DataProvider.class, RedmineLoaders.class, "getProjects", ((RedmineConfig) config).getServerInfo()),
				Interfaces.fromMethod(SimpleCallback.class, this, "showProjectInfo"), 
				Interfaces.fromMethod(DataProvider.class, this, "loadQueries")));
        addToRightColumn(new FieldsMappingPanel(RedmineSupportedFields.SUPPORTED_FIELDS, config.getFieldMappings()));
    }
    
    /**
     * Loads queries.
     * @return queries to load.
     * @throws ValidationException 
     */
    List<? extends NamedKeyedObject> loadQueries() throws ValidationException {
    	final RedmineConfig config = (RedmineConfig) this.config;
    	try {
    		return RedmineLoaders.loadData(config.getServerInfo(), config.getProjectKey());
    	} catch (NotFoundException e) {
            EditorUtil.show(getWindow(), "Can't load Saved Queries", "The server did not return any saved queries.\n" +
                    "NOTE: This operation is only supported by Redmine 1.3.0+");
            return null;
    	} catch (ValidationException e) {
    		throw e;
        } catch (Exception e) {
        	throw new RuntimeException(e);
		}
	}

	void showProjectInfo() throws ValidationException {
        WebConfig webConfig = (WebConfig) config;
        if (!webConfig.getServerInfo().isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }
        if (webConfig.getProjectKey() == null || webConfig.getProjectKey().isEmpty()) {
            throw new ValidationException("Please, provide the project key first");
        }
		notifyProjectLoaded(RedmineLoaders.loadProject(getRedmineManager(),
				webConfig.getProjectKey()));
	}

	RedmineManager getRedmineManager() {
        RedmineManager mgr;
        final WebConfig wc = (WebConfig) config;
        final WebServerInfo serverInfo = wc.getServerInfo();
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

    class RedmineServerPanel extends Panel implements Validatable {
        private static final String PANEL_CAPTION = "Redmine Server Info";
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
            super(PANEL_CAPTION);
            buildUI();
            addListener();
        }

        private void buildUI() {
            setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
            
			final WebServerInfo serverInfo = ((RedmineConfig) config)
					.getServerInfo();

            GridLayout layout = new GridLayout();
            addComponent(layout);
            layout.setSpacing(true);
            layout.setMargin(true);
            layout.setColumns(2);
            layout.setRows(8);

            int currentRow = 0;

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
			serverURL.setPropertyDataSource(new MethodProperty<String>(
					serverInfo, "host"));            

            layout.addComponent(serverURL, 1, 0);
            
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
			redmineAPIKey.setPropertyDataSource(new MethodProperty<String>(
                    serverInfo, "apiKey"));

            Label loginLabel = new Label("Login:");
            layout.addComponent(loginLabel, 0, currentRow);
            layout.setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

            login = new TextField();
            login.addStyleName("server-panel-textfield");
			login.setPropertyDataSource(new MethodProperty<String>(serverInfo,
					"userName"));
            layout.addComponent(login, 1, currentRow);
            layout.setComponentAlignment(login, Alignment.MIDDLE_LEFT);
            currentRow++;

            Label passwordLabel = new Label("Password:");
            layout.addComponent(passwordLabel, 0, currentRow);
            layout.setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

            password = new PasswordField();
            password.addStyleName("server-panel-textfield");
			password.setPropertyDataSource(new MethodProperty<String>(serverInfo,
					"password"));
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
                    ((RedmineConfig) config).getServerInfo().setUseAPIKeyInsteadOfLoginPassword(useAPIOptionSelected);
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
                throw new ValidationException("Server URL is not set");
            }
        }

        private void checkProtocol() {
            String serverURLValue = (String) serverURL.getValue();
            if (!serverURLValue.trim().isEmpty() && !serverURLValue.startsWith("http")) {
                serverURL.setValue(WebServerInfo.DEFAULT_URL_PREFIX + serverURLValue);
            }
        }
    }

    class OtherRedmineFieldsPanel extends Panel {
        private static final String OTHER_PANEL_CAPTION = "Additional Info";
        private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

        private ConfigEditor configEditor;

        public OtherRedmineFieldsPanel(ConfigEditor configEditor, RedmineConfig config) {
            super(OTHER_PANEL_CAPTION);
            this.configEditor = configEditor;

            buildUI(config);
        }

        private void buildUI(final RedmineConfig config) {
            setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
            setHeight("157px");

            setSpacing(true);
            setMargin(true);

            HorizontalLayout taskTypeLayout = new HorizontalLayout();
            //taskTypeLayout.setSizeUndefined();
            taskTypeLayout.setSpacing(true);

            final TextField defaultTaskType = EditorUtil.addLabeledText(taskTypeLayout, "Default task type:",
                    "New tasks will be created with this 'tracker' (bug/task/support/feature/...)");
            defaultTaskType.setWidth("200px");
			final MethodProperty<String> taskTypeProperty = new MethodProperty<String>(
					config, "defaultTaskType");
			defaultTaskType.setPropertyDataSource(taskTypeProperty);

            Button showDefaultTaskType = EditorUtil.createLookupButton(
                    configEditor,
                    "...",
                    "Show list of available tracker types on the Redmine server",
                    "Select task type",
                    "List of available task types on the Redmine server",
                    new DataProvider<List<? extends NamedKeyedObject>>() {
						@Override
						public List<? extends NamedKeyedObject> loadData()
								throws ValidationException {
							try {
								return RedmineLoaders.loadTrackers(config);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					},
                    taskTypeProperty,
                    true
            );
            taskTypeLayout.addComponent(showDefaultTaskType);
            addComponent(taskTypeLayout);

			addComponent(Editors
					.createFindUsersElement(new MethodProperty<Boolean>(config,
							"findUserByName")));

            final CheckBox saveRelations = new CheckBox(SAVE_ISSUE_LABEL);
			saveRelations.setPropertyDataSource(new MethodProperty<Boolean>(
					config, "saveIssueRelations"));
            addComponent(saveRelations);
        }
    }
}
