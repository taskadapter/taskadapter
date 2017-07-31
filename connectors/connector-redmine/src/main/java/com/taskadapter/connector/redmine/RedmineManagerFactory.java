package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.TransportConfiguration;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

public final class RedmineManagerFactory {
    public static RedmineManager createRedmineManager(WebConnectorSetup setup) {
        final PoolingClientConnectionManager insecureConnectionManager;
        try {
            insecureConnectionManager = com.taskadapter.redmineapi.RedmineManagerFactory.createInsecureConnectionManager();
        } catch (Exception e) {
            throw new RuntimeException("cannot create a connection manager for insecure SSL connections", e);
        }
        final TransportConfiguration ignoredSslConfig = com.taskadapter.redmineapi.RedmineManagerFactory.createShortTermConfig(insecureConnectionManager);

        if (setup.useApiKey()) {
            return com.taskadapter.redmineapi.RedmineManagerFactory.createWithApiKey(setup.host(), setup.apiKey(), ignoredSslConfig);
        } else {
            return com.taskadapter.redmineapi.RedmineManagerFactory.createWithUserAuth(setup.host(),
                    setup.userName(), setup.password(), ignoredSslConfig);
        }
    }
}
