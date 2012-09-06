package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueLink;
import com.google.common.base.Strings;
import com.taskadapter.connector.definition.PriorityResolver;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JiraToGTask {
    private static final Logger logger = LoggerFactory.getLogger(JiraToGTask.class);

    private PriorityResolver priorityResolver;

    public JiraToGTask(PriorityResolver priorityResolver) {
        this.priorityResolver = priorityResolver;
    }

    public List<GTask> convertToGenericTaskList(List<Issue> issues) {
        // TODO see http://jira.atlassian.com/browse/JRA-6896
//        logger.info("Jira: no tasks hierarchy is supported");

        List<GTask> rootLevelTasks = new ArrayList<GTask>();

        for (Issue issue : issues) {
            GTask genericTask = convertToGenericTask(issue);
            rootLevelTasks.add(genericTask);
        }
        return rootLevelTasks;
    }

    public GTask convertToGenericTask(Issue issue) {
        GTask task = new GTask();
        Integer intId = Integer.parseInt(issue.getId());
        task.setId(intId);
        task.setKey(issue.getKey());

        String jiraUserLogin = null;
        if (issue.getAssignee() != null) {
            jiraUserLogin = issue.getAssignee().getName();
        }

        if (jiraUserLogin != null) {
            GUser genericUser = new GUser();

            // TODO note: user ID is not set here. should we use a newer Jira API library?
            genericUser.setLoginName(jiraUserLogin);

            task.setAssignee(genericUser);
        }

        task.setType(issue.getIssueType().getName());
        task.setSummary(issue.getSummary());
        task.setDescription(issue.getDescription());

        DateTime dueDate = issue.getDueDate();
        if (dueDate != null) {
            task.setDueDate(dueDate.toDate());
        }

        // TODO set these fields as well
        // task.setEstimatedHours(issue.getEstimatedHours());
        // task.setDoneRatio(issue.getDoneRatio());

        String jiraPriorityName = null;
        if (issue.getPriority() != null) {
            jiraPriorityName = issue.getPriority().getName();
        }

        if (!Strings.isNullOrEmpty(jiraPriorityName)) {
            Integer priorityValue = priorityResolver.getPriorityNumberByName(jiraPriorityName);
            task.setPriority(priorityValue);
        }

        processRelations(issue, task);

        return task;
    }

    public GTask convertToGenericTask(BasicIssue issue) {
        GTask task = new GTask();
        Integer intId = Integer.parseInt(issue.getId());
        task.setId(intId);
        task.setKey(issue.getKey());

        // TODO set these fields as well
        // task.setEstimatedHours(issue.getEstimatedHours());
        // task.setDoneRatio(issue.getDoneRatio());

        return task;
    }

    private static void processRelations(Issue issue, GTask genericTask) {
        Iterable<IssueLink> links = issue.getIssueLinks();
        if (links != null) {
            for (IssueLink link : links) {
                if (link.isOutbound()) {
                    String name = link.getIssueLinkType().getName();
                    if (name.equals(JiraConstants.getJiraLinkNameForPrecedes())) {
                        GRelation r = new GRelation(issue.getKey(), link.getTargetIssueKey(), GRelation.TYPE.precedes);
                        genericTask.getRelations().add(r);
                    } else {
                        logger.error("relation type is not supported: " + link.getIssueLinkType());
                    }
                }
            }
        }
    }

}
