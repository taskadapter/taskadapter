package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.model.GTaskDescriptor;

public class RedmineSupportedFields {
    public static final AvailableFields SUPPORTED_FIELDS;

    static {
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        builder.addField(GTaskDescriptor.FIELD.SUMMARY, "Summary");
        builder.addField(GTaskDescriptor.FIELD.DESCRIPTION, "Description");
        builder.addField(GTaskDescriptor.FIELD.TASK_TYPE, "Tracker type");
        builder.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME, "Estimated time");
        builder.addField(GTaskDescriptor.FIELD.DONE_RATIO, "Done ratio");
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE, "Assignee");
        builder.addField(GTaskDescriptor.FIELD.DUE_DATE, "Due Date");
        builder.addField(GTaskDescriptor.FIELD.START_DATE, "Start Date");
        builder.addField(GTaskDescriptor.FIELD.TASK_STATUS, "Task status");
        SUPPORTED_FIELDS = builder.end();
    }

}
