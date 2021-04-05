package com.taskadapter.core;

import com.taskadapter.connector.redmine.CustomFieldBuilder;
import com.taskadapter.integrationtests.RedmineTestInitializer;
import com.taskadapter.model.Field;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.internal.Transport;

import java.util.List;

public class RedmineTestUtil {

    private static RedmineManager mgr = RedmineTestInitializer.mgr;
    private static Transport transport = mgr.getTransport();

    public static Issue createIssueInRedmine(Project redmineProject, String description) throws RedmineException {
        return new Issue(transport, redmineProject.getId(), "some summary")
                .setDescription(description)
                .create();
    }

    public static Issue createIssueInRedmine(Project redmineProject, String description, GUser assignee) throws RedmineException {
        return new Issue(transport, redmineProject.getId(), "some summary")
                .setDescription(description)
                .setAssigneeId(assignee.getId())
                .create();
    }

    public static Issue createIssueInRedmineWithCustomField(int projectId, Field<?> field, String value) throws RedmineException {
        List<CustomFieldDefinition> customFieldDefinitions = mgr.getCustomFieldManager().getCustomFieldDefinitions();
        Issue issue = new Issue(transport, projectId, "some summary");
        CustomFieldBuilder.add(issue, customFieldDefinitions, field, value);
        return issue.create();
    }

    public static void deleteIssue(int issueId) throws RedmineException {
        mgr.getIssueManager().deleteIssue(issueId);
    }
}
