package com.taskadapter.connector.definition;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.common.TaskSaver;

import java.util.Collection;


/**
 * All Task Adapter Data Connectors must implement this interface.
 * 
 * @author Alexey Skorokhodov
 */
public interface Descriptor {

	public enum Feature {
		LOAD_TASK,
		SAVE_TASK,
		UPDATE_TASK,
		TASK_TYPE,
		// TODO Alexey: we need to use this new Feature
		LOAD_PRIORITIES
	}
	
	public Collection<Feature> getSupportedFeatures();

	/**
	 * get the Connector ID. Once defined, the ID should not be changed in the connectors to avoid breaking compatibility.
	 */
	public String getID();
	
	public String getLabel();
	
	/**
	 * Any serverInfo the connector wishes to tell about itself, like some limitations or requirements.
	 */
	public String getDescription();
	
	public ConnectorConfig createDefaultConfig();
	
	public Class<?> getConfigClass();

	public AvailableFieldsProvider getAvailableFieldsProvider();

	public ProjectLoader getProjectLoader();
	
	public TaskSaver getTaskSaver(ConnectorConfig config);

	public TaskLoader getTaskLoader();

	public PriorityLoader getPriorityLoader();

    PluginFactory getPluginFactory();
}
