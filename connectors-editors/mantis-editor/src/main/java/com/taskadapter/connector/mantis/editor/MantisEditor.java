package com.taskadapter.connector.mantis.editor;

import java.util.List;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisDescriptor;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
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
    }

    private void buildUI() {
        // top left and right
        createServerAndProjectPanelOnTopDefault(new DataProvider<List<? extends NamedKeyedObject>>() {
			@Override
			public List<? extends NamedKeyedObject> loadData()
					throws ValidationException {
						return MantisLoaders
								.getProjects(((MantisConfig) config)
										.getServerInfo());
			}
		}, null, null);

        // left
        addToLeftColumn(new OtherMantisFieldsPanel(this));

        //right
        addToRightColumn(new FieldsMappingPanel(MantisDescriptor.instance.getAvailableFields(), config.getFieldMappings()));
    }
}
