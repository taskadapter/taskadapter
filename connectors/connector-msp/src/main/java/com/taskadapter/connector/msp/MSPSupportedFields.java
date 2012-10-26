package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.model.GTaskDescriptor;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.TaskField;

public class MSPSupportedFields {
    public static final AvailableFields SUPPORTED_FIELDS;

    static {
        final String defaultEstimatedTimeOption = MSPUtils.getEstimatedTimeOptions()[0];
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        final String defaultDueDateOption = MSPUtils.getDueDateOptions()[0];
        builder.addField(GTaskDescriptor.FIELD.SUMMARY, TaskField.SUMMARY.getName());
        builder.addField(GTaskDescriptor.FIELD.DESCRIPTION, TaskField.NOTES.getName());
        builder.addField(GTaskDescriptor.FIELD.TASK_TYPE, MSPUtils.getAllTextFieldNames()).withDefault(MSPUtils.getDefaultTaskType());
        builder.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME, MSPUtils.getEstimatedTimeOptions()).withDefault(defaultEstimatedTimeOption);
        builder.addField(GTaskDescriptor.FIELD.DONE_RATIO, TaskField.PERCENT_COMPLETE.getName());
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE, TaskField.ASSIGNMENT_OWNER.getName());
        builder.addField(GTaskDescriptor.FIELD.DUE_DATE, MSPUtils.getDueDateOptions()).unselected().withDefault(defaultDueDateOption);
        builder.addField(GTaskDescriptor.FIELD.START_DATE, MSPUtils.getStartDateOptions()).withDefault(ConstraintType.MUST_START_ON.name());
        builder.addField(GTaskDescriptor.FIELD.REMOTE_ID, MSPUtils.getAllTextFieldNames()).unselected().withDefault(MSPUtils.getDefaultRemoteIdMapping());
        builder.addField(GTaskDescriptor.FIELD.TASK_STATUS, MSPUtils.getAllTextFieldNames()).unselected().withDefault(MSPUtils.getDefaultTaskStatus());
        SUPPORTED_FIELDS = builder.end();
    }

}
