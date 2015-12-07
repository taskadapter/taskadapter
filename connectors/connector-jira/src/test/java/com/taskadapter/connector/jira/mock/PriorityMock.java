package com.taskadapter.connector.jira.mock;

import com.atlassian.jira.rest.client.api.domain.Priority;

public class PriorityMock extends Priority {
    public PriorityMock() {
        super(null, null, null, null, null, null);
    }
}
