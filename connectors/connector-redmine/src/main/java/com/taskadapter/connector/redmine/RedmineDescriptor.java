package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.Descriptors;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class RedmineDescriptor {
    private static final String INFO_TEXT = "Redmine/Chiliproject connector. Connects to Redmine servers via REST API. Supports Redmine v. 1.1+";

    /**
     * Keep it the same to enable backward compatibility for previously created config files.
     */
    private static final String ID = "Redmine REST";


    /**
     * Supported fields.
     */
    private static final AvailableFields SUPPORTED_FIELDS;
    
    static {
    	final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
    	builder.addField(FIELD.SUMMARY, "Summary");
    	builder.addField(FIELD.DESCRIPTION, "Description");
    	builder.addField(FIELD.TASK_TYPE, "Tracker type");
    	builder.addField(FIELD.ESTIMATED_TIME, "Estimated time");
    	builder.addField(FIELD.DONE_RATIO, "Done ratio");
    	builder.addField(FIELD.ASSIGNEE, "Assignee");
    	builder.addField(FIELD.DUE_DATE, "Due Date");
    	builder.addField(FIELD.START_DATE, "Start date");
    	builder.addField(FIELD.TASK_STATUS, "Task status");
    	SUPPORTED_FIELDS = builder.end();
    }
    
    public static final Descriptor instance = Descriptors
            .createPluginDescriptor(ID, RedmineConfig.DEFAULT_LABEL, INFO_TEXT,
                    SUPPORTED_FIELDS);    
}
