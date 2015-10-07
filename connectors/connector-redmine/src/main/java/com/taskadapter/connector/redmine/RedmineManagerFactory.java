package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.TransportConfiguration;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

public final class RedmineManagerFactory {
    public static RedmineManager createRedmineManager(WebServerInfo serverInfo) {
        final PoolingClientConnectionManager insecureConnectionManager;
        try {
            insecureConnectionManager = com.taskadapter.redmineapi.RedmineManagerFactory.createInsecureConnectionManager();
        } catch (Exception e) {
            throw new RuntimeException("cannot create a connection manager for insecure SSL connections", e);
        }
        final TransportConfiguration ignoredSslConfig = com.taskadapter.redmineapi.RedmineManagerFactory.createShortTermConfig(insecureConnectionManager);

        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            return com.taskadapter.redmineapi.RedmineManagerFactory.createWithApiKey(serverInfo.getHost(), serverInfo.getApiKey(), ignoredSslConfig);
        } else {
            return com.taskadapter.redmineapi.RedmineManagerFactory.createWithUserAuth(serverInfo.getHost(),
                    serverInfo.getUserName(), serverInfo.getPassword(), ignoredSslConfig);
        }
    }
}
