package com.taskadapter.connector.redmine;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;

import java.util.HashMap;
import java.util.Map;

public class RedmineConfig extends ConnectorConfig {

    static final String DEFAULT_LABEL = "Redmine";

    private static final long serialVersionUID = 1L;
    private static final String TASK_TYPE_BUG = "Bug";

    private static final Map<String, Integer> DEFAULT_PRIORITIES = new HashMap<>();
    static {
        DEFAULT_PRIORITIES.put("Low", 100);
        DEFAULT_PRIORITIES.put("Normal", 500);
        DEFAULT_PRIORITIES.put("High", 700);
        DEFAULT_PRIORITIES.put("Urgent", 800);
        DEFAULT_PRIORITIES.put("Immediate", 1000);
    }

    private WebServerInfo serverInfo = new WebServerInfo();

    private String defaultTaskStatus = "New";
    private boolean findUserByName;
    private Integer queryId;
    private String projectKey;

    public RedmineConfig() {
        super(new HashMap<>(DEFAULT_PRIORITIES));
        setLabel(DEFAULT_LABEL);
        setDefaultTaskType(TASK_TYPE_BUG);
    }

    public String getDefaultTaskStatus() {
        return defaultTaskStatus;
    }

    public void setDefaultTaskStatus(String defaultTaskStatus) {
        this.defaultTaskStatus = defaultTaskStatus;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hashCode(getDefaultTaskType(), defaultTaskStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof RedmineConfig) {
            RedmineConfig other = (RedmineConfig) obj;
            return Objects.equal(getDefaultTaskType(),
                    other.getDefaultTaskType())
                    && Objects
                            .equal(defaultTaskStatus, other.defaultTaskStatus);
        } else {
            return false;
        }
    }

    public WebServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(WebServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public boolean isFindUserByName() {
        return findUserByName;
    }

    public void setFindUserByName(boolean findUserByName) {
        this.findUserByName = findUserByName;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getQueryIdStr() {
        return queryId == null ? "" : queryId.toString();
    }

    public void setQueryIdStr(String id) {
        if (id == null || id.isEmpty())
            this.queryId = null;
        else
            this.queryId = Integer.parseInt(id);
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public static Priorities generateDefaultPriorities() {
        return new Priorities(new HashMap<>(DEFAULT_PRIORITIES));
    }
}
