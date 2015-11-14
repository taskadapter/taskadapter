package com.taskadapter.connector.jira;

import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraUserConverter {
    private Map<String, String> cachedJiraUsers = new HashMap<>();
    private final JiraConnection connection;

    public JiraUserConverter(JiraConnection connection) {
        this.connection = connection;
    }

    // TODO implement a mock JiraConnection and test with a task with NULL assignee.
    public List<GTask> convertAssignees(List<GTask> tasks) throws RemoteException {
        for (GTask task : tasks) {
            if (task.getAssignee() != null) {
                setAssigneeDisplayName(task);
            }
        }
        return tasks;
    }

    // TODO this will probably fail if the current Jira user is not Admin
    public GTask setAssigneeDisplayName(GTask task) {
        GUser assignee = task.getAssignee();
        if (assignee != null) {
            String loginName = assignee.getLoginName();
            String assigneeFullname = cachedJiraUsers.get(loginName);
            if (assigneeFullname == null || assigneeFullname.length() == 0) {
                assigneeFullname = connection.getUser(loginName).getDisplayName();
                cachedJiraUsers.put(loginName, assigneeFullname);
            }
            assignee.setDisplayName(assigneeFullname);
            assignee.setLoginName(loginName);
        }
        return task;
    }

}
