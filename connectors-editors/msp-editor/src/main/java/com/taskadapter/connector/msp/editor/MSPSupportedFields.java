package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.model.GTaskDescriptor;
import net.sf.mpxj.TaskField;

public class MSPSupportedFields {
    static final AvailableFields SUPPORTED_FIELDS;

    static {
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        builder.addField(GTaskDescriptor.FIELD.SUMMARY, TaskField.SUMMARY.getName());
        builder.addField(GTaskDescriptor.FIELD.DESCRIPTION, TaskField.NOTES.getName());
        builder.addField(GTaskDescriptor.FIELD.TASK_TYPE, MSPUtils.getAllTextFieldNames());
        builder.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME, MSPUtils.getEstimatedTimeOptions());
        builder.addField(GTaskDescriptor.FIELD.DONE_RATIO, TaskField.PERCENT_COMPLETE.getName());
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE, TaskField.ASSIGNMENT_OWNER.getName());
        builder.addField(GTaskDescriptor.FIELD.DUE_DATE, MSPUtils.getDueDateOptions());
        builder.addField(GTaskDescriptor.FIELD.START_DATE, MSPUtils.getStartDateOptions());
        builder.addField(GTaskDescriptor.FIELD.REMOTE_ID, MSPUtils.getAllTextFieldNames());
        builder.addField(GTaskDescriptor.FIELD.TASK_STATUS, MSPUtils.getAllTextFieldNames());
        SUPPORTED_FIELDS = builder.end();
    }

}
