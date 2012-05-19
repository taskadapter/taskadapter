package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.vaadin.ui.CheckBox;

public class GithubEditor extends ConfigEditor {

    public GithubEditor(ConnectorConfig config) {
        super(config);
        buildUI();
        setData(config);
    }

    private void buildUI() {
        addServerPanel();
        serverPanel.disableServerURLField();
        addProjectPanel(this, new GithubProjectProcessor(this));
        projectPanel.setProjectKeyLabel("Repository ID");
        projectPanel.hideQueryId();
        addSaveRelationSection();
        addFieldsMappingPanel(GithubDescriptor.instance.getAvailableFieldsProvider());
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        return config;
    }

    private void addSaveRelationSection() {
        CheckBox saveRelations = new CheckBox("Save issue relations (follows/precedes)");
        addComponent(saveRelations);
    }
}
