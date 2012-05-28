package com.taskadapter.connector.definition;

import com.taskadapter.connector.common.ProjectLoader;

/**
 * @author Alexey Skorokhodov
 */
/*
 * TODO: Maybe get rid of this class? Configure binding between descriptor
 * and service in a config file? Or, at least, remove descriptor from this
 * plugin factory and leave this as a "connector factory" item.
 */
public interface PluginFactory {
    public Connector<ConnectorConfig> createConnector(ConnectorConfig config);

    //TODO delete method with the same name from Connector class.
    public Descriptor getDescriptor();

    // TODO: move it to connector, but it must have proper configs (at least - partial).
    public ProjectLoader getProjectLoader();

}
