package com.taskadapter.connector.jira;

import com.taskadapter.model.GTask;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraUserConverter {
    private Map<String, String> cachedJiraUsers = new HashMap<String, String>();
    private final JiraConnection connection;

    public JiraUserConverter(JiraConnection connection) {
        this.connection = connection;
    }

    public List<GTask> convertAssignees(List<GTask> tasks) throws RemoteException {
        for (GTask task : tasks) {
            setAssigneeDisplayName(task);
        }
        return tasks;
    }

    // TODO this will probably fail if the current Jira user is not Admin
    public GTask setAssigneeDisplayName(GTask task) throws RemoteException {
        String loginName = task.getAssignee().getLoginName();
        String assigneeFullname = cachedJiraUsers.get(loginName);
        if (assigneeFullname == null || assigneeFullname.length() == 0) {
            assigneeFullname = connection.getUser(loginName).getFullname();
            cachedJiraUsers.put(loginName, assigneeFullname);
        }
        task.getAssignee().setDisplayName(assigneeFullname);
        task.getAssignee().setLoginName(loginName);
        return task;
    }

}
