package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.MappingFactory;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class DefaultMantisMappings {
    private static final FIELD[] DEFAULT_FIELDS = { FIELD.SUMMARY,
            FIELD.DESCRIPTION, FIELD.ASSIGNEE, FIELD.DUE_DATE };
    
	static Mappings generate() {
	    return MappingFactory.createWithEnabled(DEFAULT_FIELDS);
	}

}
