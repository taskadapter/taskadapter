package com.taskadapter.connector.jira.mock;

import com.atlassian.jira.rest.client.api.domain.Version;

public class VersionMock extends Version {
    public VersionMock() {
        super(null, null, null, null, false, false, null);
    }
}
