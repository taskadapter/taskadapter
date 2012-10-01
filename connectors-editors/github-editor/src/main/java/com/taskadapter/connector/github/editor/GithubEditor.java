package com.taskadapter.connector.github.editor;


import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.ServerPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;

public class GithubEditor extends TwoColumnsConfigEditor {

    public GithubEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    @SuppressWarnings("unchecked")
	private void buildUI() {
        // top left and right
        final WebServerInfo serverInfo = ((GithubConfig) config).getServerInfo();
		createServerAndProjectPanelOnTopDefault(
        		EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "queryString")),
        		Interfaces.fromMethod(DataProvider.class, GithubLoaders.class, 
        				  "getProjects", serverInfo)
        		, null, null,
                ((GithubConfig) config).getServerInfo());

        final ServerPanel serverPanel = getPanel(ServerPanel.class);
        serverPanel.disableServerURLField();

        final ProjectPanel projectPanel =  getPanel(ProjectPanel.class);
        projectPanel.setProjectKeyLabel("Repository ID");

        addToLeftColumn(new OtherGithubFieldsPanel(this));
        addToRightColumn(new FieldsMappingPanel(GithubSupportedFields.SUPPORTED_FIELDS, config.getFieldMappings()));
    }
}
