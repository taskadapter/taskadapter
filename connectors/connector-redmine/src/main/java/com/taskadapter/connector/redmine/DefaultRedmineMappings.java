package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.MappingFactory;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class DefaultRedmineMappings {
    
    private static FIELD[] DEFAULT_FIELDS = {
        FIELD.SUMMARY, FIELD.TASK_TYPE, FIELD.TASK_STATUS, FIELD.ESTIMATED_TIME,
        FIELD.DONE_RATIO, FIELD.ASSIGNEE, FIELD.DESCRIPTION, FIELD.START_DATE,
        FIELD.DUE_DATE
    };
    
    public static Mappings generate() {
        // TODO !!! this will generate Task Type field with "Task Type" text shown in the UI later,
        // while the actual field name for Redmine is "Tracker Type". Need to do the same
        // manual mapping as DefaultMSPMapping class does: set field names one-by-one.
        return MappingFactory.createWithEnabled(DEFAULT_FIELDS);
    }
}
