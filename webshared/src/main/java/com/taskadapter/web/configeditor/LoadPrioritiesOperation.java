package com.taskadapter.web.configeditor;

/**
 * @author Alexey Skorokhodov
 */

import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

public class LoadPrioritiesOperation extends LookupOperation {

	public LoadPrioritiesOperation(ConfigEditor editor, PluginFactory factory) {
		super(editor, factory);
	}

	@Override
	protected List<? extends NamedKeyedObject> loadData() throws Exception {
		return factory.getDescriptor().getPriorityLoader().getPriorities(config.getServerInfo());
	}
}
