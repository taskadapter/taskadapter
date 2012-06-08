package com.taskadapter.connector.github;


import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.connector.definition.WebServerInfo;
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
        final WebServerInfo serverInfo = ((WebConfig) config).getServerInfo();
		createServerAndProjectPanelOnTopDefault(
        		EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")), null,
        		Interfaces.fromMethod(DataProvider.class, GithubLoaders.class, 
        				  "getProjects", serverInfo)
        		, null, null);

        final ServerPanel serverPanel = getPanel(ServerPanel.class);
        serverPanel.disableServerURLField();

        final ProjectPanel projectPanel =  getPanel(ProjectPanel.class);
        projectPanel.setProjectKeyLabel("Repository ID");

        // left
        addToLeftColumn(new OtherGithubFieldsPanel(this));

        //right
        addToRightColumn(new FieldsMappingPanel(GithubDescriptor.instance.getAvailableFields(), config.getFieldMappings()));
    }
}
