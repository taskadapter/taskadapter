package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor;

public class DefaultMantisMappings {
	static Mappings generateFieldsMapping() {
		final Mappings result = new Mappings();
		result.addField(GTaskDescriptor.FIELD.SUMMARY);
		result.addField(GTaskDescriptor.FIELD.DESCRIPTION);
		result.addField(GTaskDescriptor.FIELD.ASSIGNEE);
		result.addField(GTaskDescriptor.FIELD.DUE_DATE);
		return result;

	}

}
