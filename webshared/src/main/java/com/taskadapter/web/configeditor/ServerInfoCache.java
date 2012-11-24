package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.WebServerInfo;

import java.util.ArrayList;
import java.util.List;

public class ServerInfoCache {
    private List<WebServerInfo> values = new ArrayList<WebServerInfo>();

    public void save(WebServerInfo info) {
        if (contains(values, info)) {
            values.remove(info);
        }
        values.add(info);
    }

    private boolean contains(List<WebServerInfo> list, WebServerInfo info) {
        for (WebServerInfo webServerInfo : list) {
            if (webServerInfo.getHost().equals(info.getHost()) && webServerInfo.getUserName().equals(info.getUserName())) {
                return true;
            }
        }
        return false;
    }

    public List<WebServerInfo> getValues() {
        return values;
    }
}
