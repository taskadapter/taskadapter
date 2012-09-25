package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.Descriptor;

public class GithubDescriptor {
    private static final String LABEL = "Github";
    public static final Descriptor instance = new Descriptor(GithubConnector.ID, LABEL);
}
