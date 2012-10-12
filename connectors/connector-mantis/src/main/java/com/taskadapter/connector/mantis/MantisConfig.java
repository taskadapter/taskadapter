package com.taskadapter.connector.mantis;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.WebServerInfo;

import java.util.HashMap;

public class MantisConfig extends ConnectorConfig {

    static final String DEFAULT_LABEL = "Mantis";

    private static final long serialVersionUID = 1L;
    private WebServerInfo serverInfo = new WebServerInfo();
    private String projectKey;
    private boolean findUserByName;

    public MantisConfig() {
        setLabel(DEFAULT_LABEL);
    }

    @Override
    protected Priorities generateDefaultPriorities() {
        return new Priorities(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 516389048716909610L;

            {
                put("low", 100);
                put("normal", 500);
                put("high", 700);
                put("urgent", 800);
                put("immediate", 1000);
            }
        });
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
