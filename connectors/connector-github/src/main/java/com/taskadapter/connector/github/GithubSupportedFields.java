package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.model.GTaskDescriptor;

public class GithubSupportedFields {
    public static final AvailableFields SUPPORTED_FIELDS;

    static {
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        builder.addField(GTaskDescriptor.FIELD.SUMMARY, "Summary");
        builder.addField(GTaskDescriptor.FIELD.DESCRIPTION, "Description");
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE, "Assignee");
        builder.addField(GTaskDescriptor.FIELD.START_DATE, "Start Date");
        SUPPORTED_FIELDS = builder.end();
    }


}
