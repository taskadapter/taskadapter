package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.mantis.MantisDescriptor;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class MantisEditor extends TwoColumnsConfigEditor {

    public MantisEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
        setData(config);
    }

    private void buildUI() {
        // top left and right
        createServerAndProjectPanelOnTopDefault(new MantisProjectProcessor(this));

        // left
        addToLeftColumn(new OtherMantisFieldsPanel(this));

        //right
        fieldsMappingPanel = new FieldsMappingPanel(MantisDescriptor.instance.getAvailableFields(), config);
        addToRightColumn(fieldsMappingPanel);
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        return config;
    }
}
