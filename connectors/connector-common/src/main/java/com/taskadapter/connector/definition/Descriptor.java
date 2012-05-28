package com.taskadapter.connector.definition;

import com.taskadapter.connector.common.TaskSaver;

import java.util.Collection;


/**
 * All Task Adapter Data Connectors must implement this interface.
 *
 * @author Alexey Skorokhodov
 */
/*
 * TODO: Get rid of "implementation" interfaces, use a "plain data" class.
 * Maybe use properties for this task?
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
     * Any text the connector wants to tell about itself, like some limitations or requirements.
     */
    public String getDescription();

    public ConnectorConfig createDefaultConfig();

    public Class<? extends ConnectorConfig> getConfigClass();

    public AvailableFields getAvailableFields();

    public TaskSaver getTaskSaver(ConnectorConfig config);
}
