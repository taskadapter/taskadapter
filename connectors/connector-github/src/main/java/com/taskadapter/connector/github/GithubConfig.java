package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;

import java.util.Collections;
import java.util.Map;

public class GithubConfig extends ConnectorConfig {
    
    private static final Map<String, Integer> DEFAULT_PRIORITIES = Collections
            .emptyMap();
    
    static final String DEFAULT_LABEL = "Github";

    private String issueState;
    private String issueKeyword;
    private String queryString;
    private WebServerInfo serverInfo = new WebServerInfo();
    private String projectKey;

    public GithubConfig() {
        super(DEFAULT_PRIORITIES);
        setLabel(DEFAULT_LABEL);
        getServerInfo().setHost("http://github.com");
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

    public WebServerInfo getServerInfo() {
        return serverInfo;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
