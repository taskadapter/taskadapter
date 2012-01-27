package com.taskadapter.connector.github;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GTaskDescriptor;

import java.util.HashMap;
import java.util.Map;

public class GithubConfig extends WebConfig {
    private static final String DEFAULT_LABEL = "Github";

    private String issueState;

    private String issueKeyword;

    public GithubConfig () {
        label = DEFAULT_LABEL;
    }

    @Override
    protected Map<GTaskDescriptor.FIELD, Mapping> generateDefaultFieldsMapping() {
		Map<GTaskDescriptor.FIELD, Mapping> fieldsMapping = new HashMap<GTaskDescriptor.FIELD, Mapping>();
        fieldsMapping.put(GTaskDescriptor.FIELD.START_DATE, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.SUMMARY, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.ASSIGNEE, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.DESCRIPTION, new Mapping());
		return fieldsMapping;
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

    @Override
	protected Priorities generateDefaultPriorities() {
		return new Priorities(new HashMap<String, Integer>() {
			private static final long serialVersionUID = 516389048716909610L;
			{
				// EMPTY! Github does not support priorities for issues
			}
		});
	}}
