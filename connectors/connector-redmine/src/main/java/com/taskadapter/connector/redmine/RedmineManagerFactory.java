package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.redmineapi.RedmineManager;

public class RedmineManagerFactory {
    public static RedmineManager createRedmineManager(WebServerInfo serverInfo) {
        RedmineManager mgr;
        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            mgr = com.taskadapter.redmineapi.RedmineManagerFactory.createWithApiKey(serverInfo.getHost(), serverInfo.getApiKey());
        } else {
            mgr = com.taskadapter.redmineapi.RedmineManagerFactory.createWithUserAuth(serverInfo.getHost(),
                    serverInfo.getUserName(), serverInfo.getPassword());
        }
        return mgr;
    }
}
