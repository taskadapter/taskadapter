package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

public class MantisFactory implements PluginFactory {
    @Override
    public Connector createConnector(ConnectorConfig config) {
        return new MantisConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return MantisDescriptor.instance;
    }
    
    @Override
    public ProjectLoader getProjectLoader() {
        return new MantisProjectLoader();
    }
    
    @Override
    public PriorityLoader getPriorityLoader() {
        throw new RuntimeException("NOT READY");
    }

}
