package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.MappingFactory;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class DefaultGithubMappings {
    
    private static final FIELD[] DEFAULT_FIELDS = {
        FIELD.START_DATE, FIELD.DUE_DATE, FIELD.SUMMARY, FIELD.ASSIGNEE, FIELD.DESCRIPTION
    }; 
    
	static Mappings generate() {
	    return MappingFactory.createWithEnabled(DEFAULT_FIELDS);
	}
}
