package com.taskadapter.connector.definition;

import com.google.common.base.Objects;

import java.util.Map;
import java.util.TreeMap;


abstract public class WebConfig extends ConnectorConfig {
	private static final long serialVersionUID = 1L;

	private WebServerInfo serverInfo = new WebServerInfo();
	protected Integer queryId;
	protected String projectKey;
	protected Map<String, String> customFields = new TreeMap<String, String>();
    protected Boolean findUserByName = Boolean.FALSE;

	public WebConfig(String label) {
		super(label);
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

    public Boolean getFindUserByName() {
   		return findUserByName;
   	}

   	public void setFindUserByName(Boolean find) {
   		this.findUserByName = find;
   	}

    public ProjectInfo getProjectInfo() {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectKey(projectKey);
        projectInfo.setQueryId(queryId);
        return projectInfo;
    }

    @Override
    public int hashCode() {
        return  31 * super.hashCode() +
                Objects.hashCode(customFields, projectKey, queryId, serverInfo, findUserByName);
    }

	@Override
	public boolean equals(Object obj) {
        if(!super.equals(obj)){
            return false;
        }
        if(obj instanceof WebConfig){
            WebConfig other = (WebConfig) obj;
            return Objects.equal(customFields, other.customFields) &&
                    Objects.equal(projectKey, other.projectKey) &&
                    Objects.equal(queryId, other.queryId) &&
                    Objects.equal(serverInfo, other.serverInfo) &&
                    Objects.equal(findUserByName, other.findUserByName);
        }  else {
            return false;
        }

	}
}
