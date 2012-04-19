package com.taskadapter.connector.redmine;

import org.redmine.ta.RedmineManager;

import com.taskadapter.connector.definition.WebServerInfo;

public class RedmineManagerFactory {
    public static RedmineManager createRedmineManager(WebServerInfo serverInfo) {
        RedmineManager mgr;
        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            mgr = new RedmineManager(serverInfo.getHost(), serverInfo.getApiKey());
        } else {
            mgr = new RedmineManager(serverInfo.getHost());
            mgr.setLogin(serverInfo.getUserName());
            mgr.setPassword(serverInfo.getPassword());
        }
        return mgr;
    }

}
