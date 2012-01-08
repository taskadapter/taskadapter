package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.Arrays;
import java.util.Collection;

public class GithubFieldsProvider implements AvailableFieldsProvider {
    public String[] getAllowedValues(GTaskDescriptor.FIELD field) {
        switch (field) {
            case SUMMARY:
                return new String[]{"Summary"};
            case DESCRIPTION:
                return new String[]{"Description"};
            case ASSIGNEE:
                return new String[]{"Assignee"};
            case START_DATE:
                return new String[]{"Start date"};
            default:
                return new String[0];
        }
    }
    
	@Override
	public Collection<FIELD> getSupportedFields() {
		return Arrays.asList(FIELD.SUMMARY, FIELD.DESCRIPTION, FIELD.ASSIGNEE, FIELD.START_DATE);
	}

}
