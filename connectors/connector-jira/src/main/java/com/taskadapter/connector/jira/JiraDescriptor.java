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

    private static final String INFO = "Atlassian Jira connector (supports Jira v. 3.1.12+)";
    
    /**
     * Supported fields.
     */
    private static final AvailableFields SUPPORTED_FIELDS;
    
    static {
    	final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
    	builder.addField(FIELD.SUMMARY, "Summary");
    	builder.addField(FIELD.DESCRIPTION, "Description");
    	builder.addField(FIELD.TASK_TYPE, "Issue type");
    	builder.addField(FIELD.ASSIGNEE, "Assignee");
    	builder.addField(FIELD.DUE_DATE, "Due Date");
    	builder.addField(FIELD.PRIORITY, "Priority");
    	SUPPORTED_FIELDS = builder.end();
    }

    public static final Descriptor instance = new Descriptor(ID, JiraConfig.DEFAULT_LABEL, INFO, SUPPORTED_FIELDS);
}
