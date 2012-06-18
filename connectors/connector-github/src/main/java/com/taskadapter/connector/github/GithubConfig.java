package com.taskadapter.connector.github;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GTaskDescriptor;

import java.util.HashMap;

public class GithubConfig extends WebConfig {
    static final String DEFAULT_LABEL = "Github";

    private String issueState;
    private String issueKeyword;
    private String queryString;

    public GithubConfig() {
        super(DEFAULT_LABEL);
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
        super.validateForLoad();
        if(getServerInfo().getUserName().isEmpty()) {
            throw new ValidationException("User login name is required.");
        }
    }

}
