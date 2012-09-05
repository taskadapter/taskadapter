package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.Priority;

public class PriorityMock extends Priority {
    public PriorityMock() {
        super(null, null, null, null, null, null);
    }
}
