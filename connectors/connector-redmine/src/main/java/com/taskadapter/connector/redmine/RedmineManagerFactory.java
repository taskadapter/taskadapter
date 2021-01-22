package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.redmineapi.RedmineManager;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;

public class RedmineManagerFactory {
    public static HttpClient createRedmineHttpClient() {
        try {
            ClientConnectionManager insecureConnectionManager = com.taskadapter.redmineapi.RedmineManagerFactory.createInsecureConnectionManager();
            return com.taskadapter.redmineapi.RedmineManagerFactory.getNewHttpClient(insecureConnectionManager);
        } catch (Exception e) {
            throw new RuntimeException("cannot create a connection manager for insecure SSL connections", e);
        }
    }

    public static RedmineManager createRedmineManager(WebConnectorSetup setup, HttpClient client) {
        if (setup.useApiKey()) {
            return com.taskadapter.redmineapi.RedmineManagerFactory.createWithApiKey(setup.host(), setup.apiKey(), client);
        }
        return com.taskadapter.redmineapi.RedmineManagerFactory.createWithUserAuth(setup.host(), setup.userName(), setup.password(), client);
    }
}
