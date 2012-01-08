package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.Arrays;
import java.util.Collection;

public class MantisAvailableFieldsProvider implements AvailableFieldsProvider {

	@Override
	public String[] getAllowedValues(FIELD field) {
		switch (field) {
		case SUMMARY:
			return new String[] { "Summary" };
		case DESCRIPTION:
			return new String[] { "Description" };
		case ASSIGNEE:
			return new String[] { "Assignee" };
		case DUE_DATE:
			return new String[] { "Due Date" };
		default:
			return new String[] {};
		}
	}

	@Override
	public Collection<FIELD> getSupportedFields() {
		return Arrays.asList(new FIELD[] {FIELD.SUMMARY, FIELD.DESCRIPTION, FIELD.ASSIGNEE, FIELD.DUE_DATE} );
	}

}
