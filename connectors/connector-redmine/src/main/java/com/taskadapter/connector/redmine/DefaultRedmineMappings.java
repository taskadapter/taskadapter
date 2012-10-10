package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.MappingFactory;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

class DefaultRedmineMappings {
    
    private static FIELD[] DEFAULT_FIELDS = {
        FIELD.SUMMARY, FIELD.TASK_TYPE, FIELD.TASK_STATUS, FIELD.ESTIMATED_TIME,
        FIELD.DONE_RATIO, FIELD.ASSIGNEE, FIELD.DESCRIPTION, FIELD.START_DATE,
        FIELD.DUE_DATE
    };
    
    static Mappings generate() {
        return MappingFactory.createWithEnabled(DEFAULT_FIELDS);
    }
}
