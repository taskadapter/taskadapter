package com.taskadapter.web.configeditor;

/**
 * @author Alexey Skorokhodov
 */

import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

public class LoadPrioritiesOperation extends LookupOperation {

	public LoadPrioritiesOperation(ConfigEditor editor, Descriptor descriptor) {
		super(editor, descriptor);
	}

	@Override
	protected List<? extends NamedKeyedObject> loadData() throws Exception {
		return descriptor.getPriorityLoader().getPriorities(config.getServerInfo());
	}
}
