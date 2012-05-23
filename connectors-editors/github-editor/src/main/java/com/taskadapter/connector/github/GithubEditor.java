package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;

public class GithubEditor extends TwoColumnsConfigEditor {

    public GithubEditor(ConnectorConfig config) {
        super(config);
        buildUI();
        setData(config);
    }

    private void buildUI() {
        // top left and right
        createServerAndProjectPanelOnTopDefault(new GithubProjectProcessor(this));
        serverPanel.disableServerURLField();
        projectPanel.setProjectKeyLabel("Repository ID");
        projectPanel.hideQueryId();

        // left
        addToLeftColumn(new OtherGithubFieldsPanel(this));

        //right
        fieldsMappingPanel = new FieldsMappingPanel(GithubDescriptor.instance.getAvailableFieldsProvider(), config);
        addToRightColumn(fieldsMappingPanel);
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        return config;
    }
}
