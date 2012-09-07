package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GTaskToGithub {

    private Map<String, User> ghUsers = new HashMap<String, User>();

    private final UserService userService;

    GTaskToGithub(UserService userService) {
        this.userService = userService;
    }

    protected Issue toIssue(GTask task) throws ConnectorException {
        Issue issue = new Issue();
        issue.setTitle(task.getSummary());
        issue.setBody(task.getDescription());
        issue.setCreatedAt(task.getCreatedOn());
        issue.setUpdatedAt(task.getUpdatedOn());
        issue.setState(task.getDoneRatio() != null && task.getDoneRatio() == 100 ? IssueService.STATE_CLOSED : IssueService.STATE_OPEN);

        if (task.getRemoteId() != null) {
            issue.setNumber(Integer.parseInt(task.getRemoteId()));
        }

        processAssignee(task, issue);
        return issue;
    }

    private void processAssignee(GTask task, Issue issue) throws ConnectorException {
        if (task.getAssignee() != null) {
            try {
                String userLogin = task.getAssignee().getLoginName();
                if (userLogin != null) {
                    if (!ghUsers.containsKey(userLogin)) {
                        User ghUser = userService.getUser(userLogin);
                        ghUser.setName(ghUser.getLogin());        // workaround for bug in eclipse-egit library - it uses name instead of login to build API request
                        ghUsers.put(userLogin, ghUser);
                    }

                    if (ghUsers.get(userLogin) != null) {
                        issue.setAssignee(ghUsers.get(userLogin));
                    }
                }
            } catch (IOException e) {
                throw GithubUtils.convertException(e);
            }
        }
    }

}
