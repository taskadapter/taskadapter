package com.taskadapter.connector.github;

import com.google.common.base.Strings;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GTaskToGithub implements ConnectorConverter<GTask, Issue> {

    private Map<String, User> ghUsers = new HashMap<>();

    private final UserService userService;
    private final Collection<GTaskDescriptor.FIELD> fieldsToExport;

    GTaskToGithub(UserService userService,  Collection<GTaskDescriptor.FIELD> fieldsToExport) {
        this.userService = userService;
        this.fieldsToExport = fieldsToExport;
    }

    Issue toIssue(GTask task) throws ConnectorException {
        Issue issue = new Issue();

        if (fieldsToExport.contains(GTaskDescriptor.FIELD.SUMMARY)) {
            issue.setTitle(task.getSummary());
        }

        if (fieldsToExport.contains(GTaskDescriptor.FIELD.DESCRIPTION)) {
            issue.setBody(task.getDescription());
        }
        issue.setCreatedAt(task.getCreatedOn());
        issue.setUpdatedAt(task.getUpdatedOn());
        // TODO add tests
        if (fieldsToExport.contains(GTaskDescriptor.FIELD.TASK_STATUS)) {
            issue.setState(task.getDoneRatio() != null && task.getDoneRatio() == 100 ? IssueService.STATE_CLOSED : IssueService.STATE_OPEN);
        }

        final String key = task.getKey();
        if (key != null) {
            final int numericKey = Integer.parseInt(key);
            issue.setNumber(numericKey);
        }

        // TODO add tests
        if (fieldsToExport.contains(GTaskDescriptor.FIELD.ASSIGNEE)) {
            processAssignee(task, issue);
        }
        return issue;
    }

    private void processAssignee(GTask task, Issue issue) throws ConnectorException {
        if (task.getAssignee() != null) {
            try {
                String userLogin = task.getAssignee().getLoginName();
                if (!Strings.isNullOrEmpty(userLogin)) {
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

    @Override
    public Issue convert(GTask source) throws ConnectorException {
        return toIssue(source);
    }

}
