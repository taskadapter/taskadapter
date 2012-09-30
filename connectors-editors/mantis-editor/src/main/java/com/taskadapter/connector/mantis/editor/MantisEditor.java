package com.taskadapter.connector.mantis.editor;


import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;

public class MantisEditor extends TwoColumnsConfigEditor {

    public MantisEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    @SuppressWarnings("unchecked")
	private void buildUI() {
        // top left and right
        createServerAndProjectPanelOnTopDefault(
        		EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
        		EditorUtil.wrapNulls(new MethodProperty<String>(config, "queryId")),
        		Interfaces.fromMethod(DataProvider.class, MantisLoaders.class, 
        				 "getProjects", ((MantisConfig) config).getServerInfo())
				, null, null);

        addToLeftColumn(new OtherMantisFieldsPanel((MantisConfig) config));
        addToRightColumn(new FieldsMappingPanel(MantisSupportedFields.SUPPORTED_FIELDS, config.getFieldMappings()));
    }
}
