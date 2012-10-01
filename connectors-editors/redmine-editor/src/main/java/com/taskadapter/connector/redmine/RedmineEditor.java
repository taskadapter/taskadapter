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
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

import java.util.List;

public class RedmineEditor extends TwoColumnsConfigEditor {

    private static final String CONNECTOR_TYPE_LABEL = "Redmine";

    public RedmineEditor(ConnectorConfig config, Services services) {
        super(config, services);

        buildUI();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        MiniServerPanel miniServerPanel = new MiniServerPanel(this, CONNECTOR_TYPE_LABEL, config);
        RedmineServerPanel redmineServerPanel = new RedmineServerPanel(this, (RedmineConfig) config);
        miniServerPanel.setServerPanel(redmineServerPanel);
        Panel panel = new Panel(miniServerPanel);
        panel.setCaption(CONNECTOR_TYPE_LABEL);
        addToLeftColumn(panel);

        addToLeftColumn(createEmptyLabel("15px"));

        OtherRedmineFieldsPanel otherPanel = new OtherRedmineFieldsPanel((RedmineConfig) config);
        addToLeftColumn(otherPanel);

        addToRightColumn(new ProjectPanel(this,
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<Integer>(config, "queryId")),
                Interfaces.fromMethod(DataProvider.class, RedmineLoaders.class, "getProjects", ((RedmineConfig) config).getServerInfo()),
                Interfaces.fromMethod(SimpleCallback.class, this, "showProjectInfo"),
                Interfaces.fromMethod(DataProvider.class, this, "loadQueries")));
        addToRightColumn(new FieldsMappingPanel(RedmineSupportedFields.SUPPORTED_FIELDS, config.getFieldMappings()));
        hideDescription();
    }

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

    class OtherRedmineFieldsPanel extends Panel {
        private static final String OTHER_PANEL_CAPTION = "Additional Info";
        private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

        public OtherRedmineFieldsPanel(RedmineConfig config) {
            super(OTHER_PANEL_CAPTION);
            buildUI(config);
        }

        private void buildUI(final RedmineConfig config) {
            setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
            setHeight("157px");

            setSpacing(true);
            setMargin(true);

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
