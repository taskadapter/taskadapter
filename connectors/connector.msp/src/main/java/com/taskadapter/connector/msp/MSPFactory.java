package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

/**
 * @author Alexey Skorokhodov
 */
public class MSPFactory implements PluginFactory {
    @Override
    public Connector createConnector(ConnectorConfig config) {
        return new MSPConnector((MSPConfig) config);
    }

    @Override
    public Descriptor getDescriptor() {
        return MSPDescriptor.instance;
    }
}
