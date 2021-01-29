package com.taskadapter.integrationtests;

import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import scala.collection.Seq;

public class RedmineTestLoader {
    /**
     * use [[com.taskadapter.connector.testlib.TestSaver]]
     */
    @Deprecated
    public static Issue loadCreatedTask(RedmineManager mgr, SaveResult result) throws RedmineException {
        Seq<TaskId> remoteKeys = result.getRemoteKeys();
        long remoteKey = remoteKeys.iterator().next().id();
        return mgr.getIssueManager().getIssueById((int) remoteKey);
    }
}
