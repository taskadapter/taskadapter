package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.WebServerInfo;

public class MantisManagerFactory {
    public static MantisManager createMantisManager(WebServerInfo serverInfo) {
        MantisManager mgr;
        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            mgr = new MantisManager(serverInfo.getHost());
        } else {
            mgr = new MantisManager(serverInfo.getHost(), serverInfo.getUserName(), serverInfo.getPassword());
        }
        return mgr;
    }
}
