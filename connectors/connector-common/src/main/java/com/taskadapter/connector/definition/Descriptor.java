package com.taskadapter.connector.definition;

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

    /**
     * get the Connector ID. Once defined, the ID should not be changed in the connectors to avoid breaking compatibility.
     */
    public String getID();

    public String getLabel();

    /**
     * Any text the connector wants to tell about itself, like some limitations or requirements.
     */
    public String getDescription();

    public AvailableFields getAvailableFields();
}
