package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.Descriptor;

public class RedmineDescriptor {
    public static final Descriptor instance = new Descriptor(RedmineConnector.ID, RedmineConfig.DEFAULT_LABEL);
}
