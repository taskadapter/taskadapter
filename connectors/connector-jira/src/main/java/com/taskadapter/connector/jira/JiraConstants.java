package com.taskadapter.connector.jira;

public class JiraConstants {
    // TODO delete this constant. see http://www.hostedredmine.com/issues/99397
    private static final String DEFAULT_PRECEDES_LINK_NAME = "Blocks";

    static String getJiraLinkNameForPrecedes() {
        return JiraConstants.DEFAULT_PRECEDES_LINK_NAME;
    }
}
