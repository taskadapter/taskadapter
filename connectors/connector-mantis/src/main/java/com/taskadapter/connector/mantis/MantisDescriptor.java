package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;


public class MantisDescriptor {


    private static final String ID = "Mantis";

    private static final String INFO = "Mantis connector (supports Mantis v. 1.1.1+)";

    /**
     * Supported fields.
     */
    private static final AvailableFields SUPPORTED_FIELDS;
    
    static {
    	final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
    	builder.addField(FIELD.SUMMARY, "Summary");
    	builder.addField(FIELD.DESCRIPTION, "Description");
    	builder.addField(FIELD.ASSIGNEE, "Assignee");
    	builder.addField(FIELD.DUE_DATE, "Due Date");
    	SUPPORTED_FIELDS = builder.end();
    }
    
    public static final Descriptor instance = new Descriptor(ID, MantisConfig.DEFAULT_LABEL, INFO, SUPPORTED_FIELDS);
    
}
