package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;

import java.util.Collections;
import java.util.Map;

public class GithubConfig extends ConnectorConfig {
    
    private static final Map<String, Integer> DEFAULT_PRIORITIES = Collections
            .emptyMap();
    
    private String issueState;
    private String issueKeyword;
    private String queryString;
    private String projectKey;

    public GithubConfig() {
        super(DEFAULT_PRIORITIES);
    }

    public String getIssueState() {
        return issueState;
    }

    public void setIssueState(String issueState) {
        this.issueState = issueState;
    }

    public String getIssueKeyword() {
        return issueKeyword;
    }

    public void setIssueKeyword(String issueKeyword) {
        this.issueKeyword = issueKeyword;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
