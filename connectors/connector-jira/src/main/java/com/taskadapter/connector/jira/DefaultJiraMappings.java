package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor;

public class DefaultJiraMappings {

    static Mappings generate() {
        final Mappings result = new Mappings();
        result.addField(GTaskDescriptor.FIELD.SUMMARY);
        result.addField(GTaskDescriptor.FIELD.TASK_TYPE);
        result.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME);
        result.addField(GTaskDescriptor.FIELD.ASSIGNEE);
        result.addField(GTaskDescriptor.FIELD.DESCRIPTION);
        result.addField(GTaskDescriptor.FIELD.DUE_DATE);
        result.addField(GTaskDescriptor.FIELD.PRIORITY);
        return result;
    }

}
