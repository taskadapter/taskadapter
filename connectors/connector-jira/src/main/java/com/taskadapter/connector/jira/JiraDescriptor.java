package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.Descriptor;

public class JiraDescriptor {
    public static final Descriptor instance = new Descriptor(JiraConnector.ID, JiraConfig.DEFAULT_LABEL);
}
