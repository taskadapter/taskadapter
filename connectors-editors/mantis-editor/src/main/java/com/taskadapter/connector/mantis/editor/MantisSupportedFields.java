package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.model.GTaskDescriptor;

public class MantisSupportedFields {

    static final AvailableFields SUPPORTED_FIELDS;

    static {
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        builder.addField(GTaskDescriptor.FIELD.SUMMARY, "Summary");
        builder.addField(GTaskDescriptor.FIELD.DESCRIPTION, "Description");
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE, "Assignee");
        builder.addField(GTaskDescriptor.FIELD.DUE_DATE, "Due Date");
        SUPPORTED_FIELDS = builder.end();
    }

}
