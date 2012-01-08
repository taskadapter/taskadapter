package com.taskadapter.connector.definition;

/**
 * @author Alexey Skorokhodov
 */
public interface PluginFactory {
	public Connector<ConnectorConfig> createConnector(ConnectorConfig config);

    //TODO delete method with the same name from Connector class.
    public Descriptor getDescriptor();
}
