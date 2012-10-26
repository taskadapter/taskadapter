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
        builder.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME);
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE);
        builder.addField(GTaskDescriptor.FIELD.DUE_DATE);
        builder.addField(GTaskDescriptor.FIELD.PRIORITY);
        SUPPORTED_FIELDS = builder.end();
    }
}
