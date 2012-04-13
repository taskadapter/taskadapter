package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

/**
 * @author Alexey Skorokhodov
 */
public class JiraFactory implements PluginFactory {
    @Override
    public Connector createConnector(ConnectorConfig config) {
        return new JiraConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return JiraDescriptor.instance;
    }
}
