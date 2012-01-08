package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.Descriptor;

import java.util.Collection;

/**
 * @author Alexey Skorokhodov
 */

public interface ProjectProcessor {
	public enum EditorFeature {
		LOAD_PROJECT_INFO,
		LOAD_PROJECTS,
		LOAD_SAVED_QUERIES
	}

	void loadProject(String projectKey);
	Descriptor getDescriptor();
	LookupOperation getLoadSavedQueriesOperation(ConfigEditor editor);
	Collection<EditorFeature> getSupportedFeatures();
}
