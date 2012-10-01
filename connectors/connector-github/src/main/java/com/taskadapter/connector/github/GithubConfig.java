package com.taskadapter.connector.github;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GTaskDescriptor;

import java.util.HashMap;

public class GithubConfig extends ConnectorConfig {
    static final String DEFAULT_LABEL = "Github";

    private String issueState;
    private String issueKeyword;
    private String queryString;
    private WebServerInfo serverInfo = new WebServerInfo();
    private String projectKey;

    public GithubConfig() {
        setLabel(DEFAULT_LABEL);
        getServerInfo().setHost("http://github.com");
    }

    @Override
    protected Mappings generateDefaultFieldsMapping() {
    	final Mappings result = new Mappings();
    	result.addField(GTaskDescriptor.FIELD.START_DATE);
    	result.addField(GTaskDescriptor.FIELD.START_DATE);
    	result.addField(GTaskDescriptor.FIELD.SUMMARY);
    	result.addField(GTaskDescriptor.FIELD.ASSIGNEE);
    	result.addField(GTaskDescriptor.FIELD.DESCRIPTION);
        return result;
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

    @Override
    protected Priorities generateDefaultPriorities() {
        return new Priorities(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 516389048716909610L;

            {
                // EMPTY! Github does not support priorities for issues
            }
        });
    }

    @Override
    public void validateForLoad() throws ValidationException {
        if (!serverInfo.isHostSet()) {
            throw new ValidationException("Server URL is not set");
        }

        if(serverInfo.getUserName().isEmpty()) {
            throw new ValidationException("User login name is required.");
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

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
