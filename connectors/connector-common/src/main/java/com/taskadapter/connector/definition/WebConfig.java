package com.taskadapter.connector.definition;

import com.google.common.base.Objects;

import java.util.Map;
import java.util.TreeMap;


abstract public class WebConfig extends ConnectorConfig {
    private static final long serialVersionUID = 1L;

    private WebServerInfo serverInfo = new WebServerInfo();
    private Integer queryId;
    private String projectKey;
    private Map<String, String> customFields = new TreeMap<String, String>();
    private boolean findUserByName;

    public WebConfig(String label) {
        super();
        setLabel(label);
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String s) {
        this.projectKey = s;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
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

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public WebServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(WebServerInfo info) {
        this.serverInfo = info;
    }

    public boolean isFindUserByName() {
        return findUserByName;
    }

    public void setFindUserByName(boolean find) {
        this.findUserByName = find;
    }

    @Override
    public void validateForSave() throws ValidationException {
        if(!serverInfo.isHostSet()) {
            throw new ValidationException("Server URL is not set");
        }
    }

    @Override
    public void validateForLoad() throws ValidationException {
        if(!serverInfo.isHostSet()) {
            throw new ValidationException("Server URL is not set");
        }
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() +
                Objects.hashCode(customFields, projectKey, queryId, serverInfo, findUserByName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof WebConfig) {
            WebConfig other = (WebConfig) obj;
            return Objects.equal(customFields, other.customFields) &&
                    Objects.equal(projectKey, other.projectKey) &&
                    Objects.equal(queryId, other.queryId) &&
                    Objects.equal(serverInfo, other.serverInfo) &&
                    Objects.equal(findUserByName, other.findUserByName);
        } else {
            return false;
        }

    }
}
