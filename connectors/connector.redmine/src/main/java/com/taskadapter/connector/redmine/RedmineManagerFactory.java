package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.WebServerInfo;
import org.redmine.ta.RedmineManager;

public class RedmineManagerFactory {
    public static RedmineManager createRedmineManager(WebServerInfo serverInfo) throws Exception {
        RedmineManager mgr;
        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            mgr = new RedmineManager(serverInfo.getHost(), serverInfo.getApiKey());
        } else {
            mgr = new RedmineManager(serverInfo.getHost(), serverInfo.getUserName(), serverInfo.getPassword());
        }
        return mgr;
    }

}
