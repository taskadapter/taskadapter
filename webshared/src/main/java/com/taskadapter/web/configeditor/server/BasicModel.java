package com.taskadapter.web.configeditor.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.taskadapter.connector.definition.WebServerInfo;

public class BasicModel implements ServerInfoModel {
    protected final List<WebServerInfo> infos;

    public BasicModel(List<WebServerInfo> infos) {
        super();
        this.infos = infos;
    }

    @Override
    public List<String> getServers() {
        final Set<String> res = new HashSet<String>();
        for (WebServerInfo wsi : infos) {
            if (wsi.getHost() != null && !wsi.getHost().isEmpty()) {
                res.add(wsi.getHost());
            }
        }
        return new ArrayList<String>(res);
    }

    @Override
    public List<String> getLogins(String url) {
        final Set<String> res = new HashSet<String>();
        for (WebServerInfo wsi : infos) {
            if (url.equals(wsi.getHost()) && wsi.getUserName() != null
                    && !wsi.getUserName().isEmpty()) {
                res.add(wsi.getHost());
            }
        }
        return new ArrayList<String>(res);
    }

    @Override
    public String getPassword(String url, String login) {
        String pwd = null;
        for (WebServerInfo wsi : infos) {
            if (url.equals(wsi.getHost()) && login.equals(wsi.getUserName())
                    && wsi.getPassword() != null
                    && !wsi.getPassword().isEmpty()) {
                if (pwd == null) {
                    pwd = wsi.getPassword();
                } else if (!pwd.equals(wsi.getPassword())) {
                    return null;
                }
            }
        }
        return pwd;
    }

    @Override
    public List<String> getLogins() {
        final Set<String> res = new HashSet<String>();
        for (WebServerInfo wsi : infos) {
            if (wsi.getUserName() != null && !wsi.getUserName().isEmpty()) {
                res.add(wsi.getHost());
            }
        }
        return new ArrayList<String>(res);
    }

    @Override
    public String getPassword(String login) {
        String pwd = null;
        for (WebServerInfo wsi : infos) {
            if (login.equals(wsi.getUserName()) && wsi.getPassword() != null
                    && !wsi.getPassword().isEmpty()) {
                if (pwd == null) {
                    pwd = wsi.getPassword();
                } else if (!pwd.equals(wsi.getPassword())) {
                    return null;
                }
            }
        }
        return pwd;
    }

}
