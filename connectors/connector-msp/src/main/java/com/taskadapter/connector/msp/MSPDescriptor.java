package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class MSPDescriptor {
    private static final String INFO = "Microsoft Project connector. Supports MPP and XML files (also known as MSPDI)";

    /**
     * Keep it the same to enable backward compatibility
     */
    public static final String ID = "Microsoft Project";

    /**
     * Supported fields.
     */
    private static final AvailableFields SUPPORTED_FIELDS;
    
    static {
    	final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
    	builder.addField(FIELD.SUMMARY, "Task Name");
    	builder.addField(FIELD.DESCRIPTION, "Notes");
    	builder.addField(FIELD.TASK_TYPE, MSPUtils.getAllTextFieldNames());
    	builder.addField(FIELD.ESTIMATED_TIME, MSPUtils.getEstimatedTimeOptions());
    	builder.addField(FIELD.DONE_RATIO, "Percent complete");
    	builder.addField(FIELD.ASSIGNEE, "Resource Name");
    	builder.addField(FIELD.DUE_DATE, MSPUtils.getDueDateOptions());
    	builder.addField(FIELD.START_DATE, MSPUtils.getStartDateOptions());
    	builder.addField(FIELD.REMOTE_ID, MSPUtils.getAllTextFieldNames());
    	builder.addField(FIELD.TASK_STATUS, MSPUtils.getAllTextFieldNames());
    	SUPPORTED_FIELDS = builder.end();
    }

    public static final Descriptor instance = new Descriptor(ID, MSPConfig.DEFAULT_LABEL, INFO, SUPPORTED_FIELDS);
}
