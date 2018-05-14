package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO why is this not used?
public class JiraUserConverter {
    private Map<String, String> cachedJiraUsers = new HashMap<>();
    private final JiraRestClient client;

    public JiraUserConverter(JiraRestClient client) {
        this.client = client;
    }

  /*  public List<GTask> convertAssignees(List<GTask> tasks) throws RemoteException {
        tasks.stream()
                .filter(task -> task.getAssignee() != null)
                .forEach(this::setAssigneeDisplayName);
        return tasks;
    }*/

    // TODO this will probably fail if the current Jira user is not Admin
/*    public GTask setAssigneeDisplayName(GTask task) {
        GUser assignee = task.getAssignee();
        if (assignee != null) {
            String loginName = assignee.getLoginName();
            String assigneeFullname = cachedJiraUsers.get(loginName);
            if (assigneeFullname == null || assigneeFullname.length() == 0) {
                final Promise<User> userPromise = client.getUserClient().getUser(loginName);
                final User user = userPromise.claim();
                assigneeFullname = user.getDisplayName();
                cachedJiraUsers.put(loginName, assigneeFullname);
            }
            assignee.setDisplayName(assigneeFullname);
            assignee.setLoginName(loginName);
        }
        return task;
    }
*/
}
