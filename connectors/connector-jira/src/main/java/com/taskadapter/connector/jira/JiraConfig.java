package com.taskadapter.connector.jira;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class JiraConfig extends ConnectorConfig {

    static final String DEFAULT_LABEL = "Atlassian Jira";

    private static final long serialVersionUID = 1L;
    private static final String TASK_TYPE_BUG = "Bug";

    private WebServerInfo serverInfo = new WebServerInfo();

    // TODO this can probably be moved to the super class
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
    private Map<String, String> customFields = new TreeMap<String, String>();

    public JiraConfig() {
        setLabel(DEFAULT_LABEL);
        setDefaultTaskType(TASK_TYPE_BUG);
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

    @Override
    protected Priorities generateDefaultPriorities() {
        return createDefaultPriorities();
    }

    /**
     * Creates a default priorities.
     *
     * @return default priorities.
     */
    public static Priorities createDefaultPriorities() {
        return new Priorities(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 516389048716909610L;

            {
                put("Trivial", 100);
                put("Minor", 300);
                put("Major", 700);
                put("Critical", 800);
                put("Blocker", 1000);
            }
        });
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

    public WebServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public String getSourceLocation() {
        return serverInfo.getHost();
    }

    @Override
    public String getTargetLocation() {
        // target is the same as source for web-based configs
        return getSourceLocation();
    }

    public void setServerInfo(WebServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }
}
