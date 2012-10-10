package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor;

class DefaultRedmineMappings {
    static Mappings generate() {
        final Mappings result = new Mappings();
        result.addField(GTaskDescriptor.FIELD.SUMMARY);
        result.addField(GTaskDescriptor.FIELD.TASK_TYPE);
        result.addField(GTaskDescriptor.FIELD.TASK_STATUS);
        result.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME);
        result.addField(GTaskDescriptor.FIELD.DONE_RATIO);
        result.addField(GTaskDescriptor.FIELD.ASSIGNEE);
        result.addField(GTaskDescriptor.FIELD.DESCRIPTION);
        result.addField(GTaskDescriptor.FIELD.START_DATE);
        result.addField(GTaskDescriptor.FIELD.DUE_DATE);
        return result;
    }
}
