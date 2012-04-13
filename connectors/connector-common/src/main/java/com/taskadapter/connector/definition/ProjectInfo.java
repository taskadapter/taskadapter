package com.taskadapter.connector.definition;

/**
 * @author Alexey Skorokhodov
 */
public class ProjectInfo {
    private String projectKey;
    private Integer queryId;

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }
}
