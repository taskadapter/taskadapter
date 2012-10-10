package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor;

public class DefaultGithubMappings {
	static Mappings generate() {
		final Mappings result = new Mappings();
		result.addField(GTaskDescriptor.FIELD.START_DATE);
		result.addField(GTaskDescriptor.FIELD.START_DATE);
		result.addField(GTaskDescriptor.FIELD.SUMMARY);
		result.addField(GTaskDescriptor.FIELD.ASSIGNEE);
		result.addField(GTaskDescriptor.FIELD.DESCRIPTION);
		return result;
	}
}
