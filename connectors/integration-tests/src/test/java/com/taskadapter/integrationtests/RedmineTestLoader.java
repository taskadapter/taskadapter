package com.taskadapter.integrationtests;

import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineTestLoader {
    /**
     * use [[com.taskadapter.connector.testlib.TestSaver]]
     */
    @Deprecated
    public static Issue loadCreatedTask(RedmineManager mgr, SaveResult result) throws RedmineException {
        var remoteKeys = result.getRemoteKeys();
        long remoteKey = remoteKeys.iterator().next().getId();
        return mgr.getIssueManager().getIssueById((int) remoteKey);
    }
}
