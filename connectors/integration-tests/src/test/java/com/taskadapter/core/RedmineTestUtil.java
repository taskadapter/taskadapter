package com.taskadapter.core;

import com.taskadapter.connector.redmine.CustomFieldBuilder;
import com.taskadapter.integrationtests.RedmineTestInitializer;
import com.taskadapter.model.Field;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.Project;

import java.util.List;

public class RedmineTestUtil {

    private static RedmineManager mgr = RedmineTestInitializer.mgr;

    public static Issue createIssueInRedmine(Project redmineProject, String description) throws RedmineException {
        Issue issue = IssueFactory.create(redmineProject.getId(), "some summary");
        issue.setDescription(description);
        return mgr.getIssueManager().createIssue(issue);
    }

    public static Issue createIssueInRedmine(Project redmineProject, String description, GUser assignee) throws RedmineException {
        Issue issue = IssueFactory.create(redmineProject.getId(), "some summary");
        issue.setDescription(description);
        issue.setAssigneeId(assignee.getId());
        return mgr.getIssueManager().createIssue(issue);
    }

    public static Issue createIssueInRedmineWithCustomField(int projectId, Field<?> field, String value) throws RedmineException {
        List<CustomFieldDefinition> customFieldDefinitions = mgr.getCustomFieldManager().getCustomFieldDefinitions();
        Issue issue = IssueFactory.create(projectId, "some summary");
        CustomFieldBuilder.add(issue, customFieldDefinitions, field, value);
        return mgr.getIssueManager().createIssue(issue);
    }

    public static void deleteIssue(int issueId) throws RedmineException {
        mgr.getIssueManager().deleteIssue(issueId);
    }
}
