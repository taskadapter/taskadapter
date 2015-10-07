package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.model.GTaskDescriptor;

public class JiraSupportedFields {
    public static final AvailableFields SUPPORTED_FIELDS;

    static {
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        builder.addField(GTaskDescriptor.FIELD.SUMMARY);
        builder.addField(GTaskDescriptor.FIELD.DESCRIPTION);
        builder.addField(GTaskDescriptor.FIELD.TASK_TYPE);
        /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
           enabled for tasks by default. let's unselect this field by default
           to avoid user confusion.
         */
        builder.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME).unselected();
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE);
        builder.addField(GTaskDescriptor.FIELD.DUE_DATE);
        builder.addField(GTaskDescriptor.FIELD.PRIORITY);
        builder.addField(GTaskDescriptor.FIELD.ENVIRONMENT);
        SUPPORTED_FIELDS = builder.end();
    }
}
