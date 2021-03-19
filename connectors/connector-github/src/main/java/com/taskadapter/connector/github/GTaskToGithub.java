package com.taskadapter.connector.github;

import com.google.common.base.Strings;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.AssigneeLoginName;
import com.taskadapter.model.CreatedOn;
import com.taskadapter.model.Description;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Summary;
import com.taskadapter.model.UpdatedOn;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GTaskToGithub implements ConnectorConverter<GTask, Issue> {
    private UserService userService;

    public GTaskToGithub(UserService userService) {
        this.userService = userService;
    }

    private Map<String, User> ghUsers = new HashMap<>();

    public Issue toIssue(GTask task) throws FieldConversionException {
        var issue = new Issue();

        for (Map.Entry<Field<?>, Object> row : task.getFields().entrySet()) {
            try {
                processField(issue, row.getKey(), row.getValue());
            } catch (Exception e) {
                throw new FieldConversionException(GithubConnector.ID, row.getKey(), row.getValue(), e.getMessage());
            }
        }

        //    if (fieldsToExport.contains(GTaskDescriptor.FIELD.TASK_STATUS));
        //      issue.setState(if (task.getDoneRatio != null && (task.getDoneRatio eq 100)) IssueService.STATE_CLOSED
        //    else IssueService.STATE_OPEN);
        issue.setState(IssueService.STATE_OPEN);

        var key = task.getKey();
        if (key != null) {
            var numericKey = Integer.parseInt(key);
            issue.setNumber(numericKey);
        }
        return issue;
    }

    private void processField(Issue issue, Field<?> field, Object value) throws ConnectorException {
        if (field instanceof Summary) {
            issue.setTitle((String) value);
            return;
        }
        if (field instanceof Description) {
            issue.setBody((String) value);
            return;
        }
        if (field instanceof AssigneeLoginName) {
            processAssigneeLoginName(issue, (String) value);
            return;
        }
        if (field instanceof CreatedOn) {
            issue.setCreatedAt((Date) value);
        }
        if (field instanceof UpdatedOn) {
            issue.setUpdatedAt((Date) value);
        }
        // unknown fields, ignore
    }

    private void processAssigneeLoginName(Issue issue, String userLogin) throws ConnectorException {
        try {
            if (!Strings.isNullOrEmpty(userLogin)) {
                if (!ghUsers.containsKey(userLogin)) {
                    var ghUser = userService.getUser(userLogin);
                    ghUsers.put(userLogin, ghUser);
                }
                if (ghUsers.get(userLogin) != null) issue.setAssignee(ghUsers.get(userLogin));
            }
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
    }

    @Override
    public Issue convert(GTask source) throws FieldConversionException {
        return toIssue(source);
    }

}
