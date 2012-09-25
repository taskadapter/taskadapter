package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class JiraDescriptor {

    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    private static final String ID = "Atlassian Jira";

    private static final AvailableFields SUPPORTED_FIELDS;
    
    static {
    	final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
    	builder.addField(FIELD.SUMMARY);
    	builder.addField(FIELD.DESCRIPTION);
    	builder.addField(FIELD.TASK_TYPE);
        builder.addField(FIELD.ESTIMATED_TIME);
        builder.addField(FIELD.ASSIGNEE);
    	builder.addField(FIELD.DUE_DATE);
    	builder.addField(FIELD.PRIORITY);
    	SUPPORTED_FIELDS = builder.end();
    }

    public static final Descriptor instance = new Descriptor(ID, JiraConfig.DEFAULT_LABEL, SUPPORTED_FIELDS);
}
