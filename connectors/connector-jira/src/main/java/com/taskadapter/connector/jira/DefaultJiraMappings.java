package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.MappingFactory;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class DefaultJiraMappings {
    
    private static final FIELD[] DEFAULT_FIELDS = { FIELD.SUMMARY,
            FIELD.TASK_TYPE, FIELD.ESTIMATED_TIME, FIELD.ASSIGNEE,
            FIELD.DESCRIPTION, FIELD.DUE_DATE, FIELD.PRIORITY };

    static Mappings generate() {
        return MappingFactory.createWithEnabled(DEFAULT_FIELDS);
    }

}
