package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class GithubEditor extends ConfigEditor {

    private TextField userNameText;

    private PasswordField passwordText;

    private TextField projectKey;

    public GithubEditor(ConnectorConfig config) throws Exception {
        super(config);
        buildUI();
        setData(config);
    }

    private void buildUI() {
        addServerPanel();
        addProjectPanel(this, new GithubProjectProcessor(this));
        addSaveRelationSection();
        addFieldsMappingPanel(GithubDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());
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
