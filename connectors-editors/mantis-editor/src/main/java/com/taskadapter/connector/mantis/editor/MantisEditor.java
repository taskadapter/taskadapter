package com.taskadapter.connector.mantis.editor;


import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;

import java.util.List;

public class MantisEditor extends TwoColumnsConfigEditor {

    private static final DataProvider<List<? extends NamedKeyedObject>> NULL_QUERY_PROVIDER = null;
    private static final SimpleCallback NULL_PROJECT_INFO_CALLBACK = null;

    public MantisEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    @SuppressWarnings("unchecked")
	private void buildUI() {
        // top left and right
        createServerAndProjectPanelOnTopDefault(
        		EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                null,
        		Interfaces.fromMethod(DataProvider.class, MantisLoaders.class, 
        				 "getProjects", ((MantisConfig) config).getServerInfo())
				, NULL_PROJECT_INFO_CALLBACK, NULL_QUERY_PROVIDER,
                ((MantisConfig) config).getServerInfo());

        addToLeftColumn(new OtherMantisFieldsPanel((MantisConfig) config));
        addToRightColumn(new FieldsMappingPanel(MantisSupportedFields.SUPPORTED_FIELDS, config.getFieldMappings()));
    }
}
