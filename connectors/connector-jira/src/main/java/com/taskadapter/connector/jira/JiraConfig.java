package com.taskadapter.connector.jira;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;

import java.util.HashMap;
import java.util.Map;

public class JiraConfig extends ConnectorConfig {

    private static final Priorities DEFAULT_PRIORITIES = createDefaultPriorities();
    
    private static final long serialVersionUID = 1L;
    private static final String TASK_TYPE_BUG = "Bug";
    private static final String DEFAULT_SUB_TASK_TYPE = "Sub-task";

    private String component = "";

    /**
     * Version ("milestone") in the project.
     */
    private String affectedVersion = "";

    /**
     * Version ("milestone") in the project.
     */
    private String fixForVersion = "";
    private String queryString;
    private Integer queryId;
    private String projectKey;
    private String defaultIssueTypeForSubtasks;

    public JiraConfig() {
        super(DEFAULT_PRIORITIES);
        setDefaultTaskType(TASK_TYPE_BUG);
        setDefaultIssueTypeForSubtasks(DEFAULT_SUB_TASK_TYPE);
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getFixForVersion() {
        return fixForVersion;
    }

    public void setFixForVersion(String fixForVersion) {
        this.fixForVersion = fixForVersion;
    }

    public String getAffectedVersion() {
        return affectedVersion;
    }

    public void setAffectedVersion(String version) {
        this.affectedVersion = version;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Creates default priorities.
     *
     * @return default priorities.
     */
    public static Priorities createDefaultPriorities() {
        final Map<String, Integer> result = new HashMap<>();
        result.put("Lowest", 100);
        result.put("Low", 300);
        result.put("Medium", 500);
        result.put("High", 700);
        result.put("Highest", 1000);
        return new Priorities(result);
    }


    @Override
    public int hashCode() {
        return 31 * super.hashCode() +
                Objects.hashCode(affectedVersion, component, getDefaultTaskType(), fixForVersion);

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof JiraConfig) {
            JiraConfig other = (JiraConfig) obj;
            return Objects.equal(affectedVersion, other.affectedVersion) &&
                    Objects.equal(component, other.component) &&
                    Objects.equal(getDefaultTaskType(), other.component) &&
                    Objects.equal(fixForVersion, other.fixForVersion);

        } else {
            return false;
        }
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

    public String getDefaultIssueTypeForSubtasks() {
        return defaultIssueTypeForSubtasks;
    }

    public void setDefaultIssueTypeForSubtasks(String defaultIssueTypeForSubtasks) {
        this.defaultIssueTypeForSubtasks = defaultIssueTypeForSubtasks;
    }
}
