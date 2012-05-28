package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
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
        return new RedmineConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

    @Override
    public ProjectLoader getProjectLoader() {
        return new RedmineProjectLoader();
    }
    
    @Override
    public PriorityLoader getPriorityLoader() {
        throw new RuntimeException("NOT READY");
    }
}
