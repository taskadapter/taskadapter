package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.Arrays;
import java.util.Collection;

public class JiraAvailableFieldsProvider implements AvailableFieldsProvider {
	public String[] getAllowedValues(GTaskDescriptor.FIELD field) {
		switch (field) {
		case SUMMARY:
			return new String[] { "Summary" };
		case DESCRIPTION:
			return new String[] { "Description" };
		case TASK_TYPE:
			return new String[] { "Issue type" };
//		case ESTIMATED_TIME:
			// not implemented yet
//			return new String[] {/* "Estimated time" */};
//		case DONE_RATIO:
			// not implemented yet
//			return new String[] {/* "Done ratio" */};
		case ASSIGNEE:
			return new String[] { "Assignee" };
		case DUE_DATE:
			return new String[] { "Due Date" };
//		case START_DATE:
			// not implemented yet
//			return new String[] {/* "Start date" */};
		case PRIORITY:
			return new String[] { "Priority" };
		default:
			return new String[] {};
		}
	}

	@Override
	public Collection<FIELD> getSupportedFields() {
		return Arrays.asList(FIELD.SUMMARY, FIELD.DESCRIPTION, FIELD.TASK_TYPE, FIELD.ASSIGNEE,
                FIELD.DUE_DATE, FIELD.PRIORITY);
	}
}