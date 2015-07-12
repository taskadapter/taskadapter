package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.input.IssueInput;

final class IssueWrapper {
    private final String key;
    private final IssueInput issueInput;

    IssueWrapper(String key, IssueInput issueInput) {
        this.key = key;
        this.issueInput = issueInput;
    }

    String getKey() {
        return key;
    }

    IssueInput getIssueInput() {
        return issueInput;
    }
}
