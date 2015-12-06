package com.taskadapter.connector.jira.mock;

import com.atlassian.jira.rest.client.api.domain.IssueType;

public class IssueTypeMock extends IssueType {
    public IssueTypeMock() {
        super(null, null, null, false, null, null);
    }
}
