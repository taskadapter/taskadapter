package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.WebConnectorSetup;

public class MantisManagerFactory {
    public static MantisManager createMantisManager(WebConnectorSetup setup) {
        if (setup.isUseApiKey()) {
            throw new RuntimeException("authorization using API key is not supported for Mantis Connector");
        }
        return new MantisManager(setup.getHost(), setup.getUserName(), setup.getPassword());
    }
}
