package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.model.GTaskDescriptor;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.TaskField;

public class MSPSupportedFields {
    public static final AvailableFields SUPPORTED_FIELDS;

    static {
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        builder.addField(GTaskDescriptor.FIELD.SUMMARY, TaskField.SUMMARY.getName());
        builder.addField(GTaskDescriptor.FIELD.DESCRIPTION, TaskField.NOTES.getName());
        builder.addField(GTaskDescriptor.FIELD.TASK_TYPE, MSPUtils.getTextFieldNamesAvailableForMapping()).withDefault(MSPUtils.getDefaultTaskType());
        builder.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME, MSPUtils.getEstimatedTimeOptions()).withDefault(MSPUtils.getEstimatedTimeDefaultMapping());
        builder.addField(GTaskDescriptor.FIELD.DONE_RATIO, TaskField.PERCENT_COMPLETE.getName());
        builder.addField(GTaskDescriptor.FIELD.ASSIGNEE, TaskField.ASSIGNMENT_OWNER.getName());
        builder.addField(GTaskDescriptor.FIELD.DUE_DATE, MSPUtils.getDueDateOptions()).unselected().withDefault(MSPUtils.getDefaultDueDate());
        builder.addField(GTaskDescriptor.FIELD.START_DATE, MSPUtils.getStartDateOptions()).withDefault(ConstraintType.MUST_START_ON.name());
        builder.addField(GTaskDescriptor.FIELD.REMOTE_ID, MSPUtils.getTextFieldNamesAvailableForMapping()).unselected().withDefault(MSPUtils.getDefaultRemoteIdMapping());
        builder.addField(GTaskDescriptor.FIELD.TASK_STATUS, MSPUtils.getTextFieldNamesAvailableForMapping()).unselected().withDefault(MSPUtils.getDefaultTaskStatus());
        builder.addField(GTaskDescriptor.FIELD.CLOSE_DATE, TaskField.ACTUAL_FINISH.getName());
        SUPPORTED_FIELDS = builder.end();
    }

}
