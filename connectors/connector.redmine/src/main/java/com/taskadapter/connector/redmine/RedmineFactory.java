package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

/**
 * @author Alexey Skorokhodov
 */
public class RedmineFactory implements PluginFactory {
    @Override
    public Connector createConnector(ConnectorConfig config) {
        return new RedmineConnector((RedmineConfig) config);
    }

    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

}
