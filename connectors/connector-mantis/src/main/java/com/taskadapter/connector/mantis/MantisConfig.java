package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;

import java.util.HashMap;
import java.util.Map;

public class MantisConfig extends ConnectorConfig {

    static final Map<String, Integer> DEFAULT_PRIORITIES = new HashMap<String, Integer>();    
    static {
        DEFAULT_PRIORITIES.put("low", 100);
        DEFAULT_PRIORITIES.put("normal", 500);
        DEFAULT_PRIORITIES.put("high", 700);
        DEFAULT_PRIORITIES.put("urgent", 800);
        DEFAULT_PRIORITIES.put("immediate", 1000);
    }
    
    static final String DEFAULT_LABEL = "Mantis";

    private static final long serialVersionUID = 1L;
    private WebServerInfo serverInfo = new WebServerInfo();
    private String projectKey;
    private boolean findUserByName;
    private Long queryId;

    public MantisConfig() {
        super(DEFAULT_PRIORITIES);
        setLabel(DEFAULT_LABEL);
    }

    public WebServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(WebServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getProjectKey() {
        return projectKey;
    }
    
    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }
    
    public String getQueryIdStr() {
        return queryId == null ? "" : queryId.toString();
    }

    public void setQueryIdStr(String id) {
        if (id == null || id.isEmpty())
            this.queryId = null;
        else
            this.queryId = Long.parseLong(id);
    }



    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public boolean isFindUserByName() {
        return findUserByName;
    }

    public void setFindUserByName(boolean findUserByName) {
        this.findUserByName = findUserByName;
    }
}
