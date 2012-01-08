package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.Arrays;
import java.util.Collection;

public class RedmineAvailableFieldsProvider implements AvailableFieldsProvider {
	public String[] getAllowedValues(GTaskDescriptor.FIELD field) {
		switch (field) {
		case SUMMARY:
			return new String[] { "Summary" };
		case DESCRIPTION:
			return new String[] { "Description" };
		case TASK_TYPE:
			return new String[] { "Tracker type" };
		case ESTIMATED_TIME:
			return new String[] { "Estimated time" };
		case DONE_RATIO:
			return new String[] { "Done ratio" };
		case ASSIGNEE:
			return new String[] { "Assignee" };
		case DUE_DATE:
			return new String[] { "Due date" };
		case START_DATE:
			return new String[] { "Start date" };
		default:
			return new String[] {};
		}
	}
	
	@Override
	public Collection<FIELD> getSupportedFields() {
		return Arrays.asList(FIELD.SUMMARY, FIELD.DESCRIPTION, FIELD.TASK_TYPE, FIELD.ESTIMATED_TIME, FIELD.DONE_RATIO,
                FIELD.ASSIGNEE, FIELD.DUE_DATE, FIELD.START_DATE);
	}

}
