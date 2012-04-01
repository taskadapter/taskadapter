package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.TaskField;

import java.util.Arrays;
import java.util.Collection;

public class MSPAvailableFieldsProvider implements AvailableFieldsProvider {
    public static final String NO_CONSTRAINT = "<no constraint>";

    @Override
    public String[] getAllowedValues(GTaskDescriptor.FIELD field) {
        switch (field) {
            case SUMMARY:
                return new String[]{"Task Name"};
            case DESCRIPTION:
                return new String[]{"Notes"};
            case TASK_TYPE:
                return MSPUtils.getAllTextFieldNames();
            case ESTIMATED_TIME:
                return getEstimatedTimeOptions();
            case DONE_RATIO:
                return new String[]{"Percent complete"};
            case ASSIGNEE:
                return new String[]{"Resource Name"};
            case DUE_DATE:
                return getDueDateOptions();
            case START_DATE:
                return getStartDateOptions();
            case REMOTE_ID:
                return MSPUtils.getAllTextFieldNames();
            case TASK_STATUS:
                return MSPUtils.getAllTextFieldNames();
            default:
                return new String[]{};
        }
    }

    public static String[] getEstimatedTimeOptions() {
        return new String[]{TaskField.DURATION.toString(), TaskField.WORK.toString()};
    }

    public static String[] getDueDateOptions() {
        return new String[]{TaskField.FINISH.toString(),
                TaskField.DEADLINE.toString()};
    }

    public static String[] getStartDateOptions() {
        String[] options = new String[ConstraintType.values().length + 1];
        options[0] = NO_CONSTRAINT;
        int i = 1;
        for (ConstraintType type : ConstraintType.values()) {
            options[i++] = type.name();
        }
        return options;
    }

    public static String getDefaultRemoteIdMapping() {
        return TaskField.TEXT22.toString();
    }

    public static String getDefaultTaskType() {
        return TaskField.TEXT23.toString();
    }

    public static String getDefaultTaskStatus() {
        return TaskField.TEXT24.toString();
    }

    @Override
    public Collection<FIELD> getSupportedFields() {
        return Arrays.asList(FIELD.SUMMARY, FIELD.DESCRIPTION, FIELD.DONE_RATIO,
                FIELD.ASSIGNEE, FIELD.DUE_DATE, FIELD.START_DATE, FIELD.TASK_TYPE, FIELD.ESTIMATED_TIME,
                FIELD.REMOTE_ID, FIELD.TASK_STATUS);
    }
}
