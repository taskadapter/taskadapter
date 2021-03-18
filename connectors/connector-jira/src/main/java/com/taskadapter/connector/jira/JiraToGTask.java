package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelationType;
import com.taskadapter.model.GTask;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JiraToGTask {
    private static final Logger logger = LoggerFactory.getLogger(JiraToGTask.class);

    private final Priorities priorities;

    public JiraToGTask(Priorities priorities) {
        this.priorities = priorities;
    }

    List<GTask> convertToGenericTaskList(CustomFieldResolver customFieldResolver, Iterable<Issue> issues) {
        // TODO see http://jira.atlassian.com/browse/JRA-6896
        // logger.info("Jira: no tasks hierarchy is supported");
        var rootLevelTasks = new ArrayList<GTask>();
        for (Issue issue : issues) {
            var genericTask = convertToGenericTask(customFieldResolver, issue);
            rootLevelTasks.add(genericTask);
        }
        return rootLevelTasks;
    }

    GTask convertToGenericTask(CustomFieldResolver customFieldResolver, Issue issue) {
        var task = new GTask();
        var longId = issue.getId();
        task.setId(longId);
        task.setKey(issue.getKey());
        // must set source system id, otherwise "update task" is impossible later
        task.setSourceSystemId(new TaskId(longId, issue.getKey()));
        var target = new ArrayList<String>();
        issue.getComponents()
                .forEach(c -> target.add(c.getName()));
        task.setValue(AllFields.components, target);
        if (issue.getAssignee() != null) {
            var assignee = issue.getAssignee();
            task.setValue(AllFields.assigneeLoginName, assignee.getName());
            task.setValue(AllFields.assigneeFullName, assignee.getDisplayName());
        }
        if (issue.getReporter() != null) {
            task.setValue(AllFields.reporterFullName, issue.getReporter().getDisplayName());
            task.setValue(AllFields.reporterLoginName, issue.getReporter().getName());
        }
        task.setValue(AllFields.taskType, issue.getIssueType().getName());
        task.setValue(AllFields.summary, issue.getSummary());
        task.setValue(AllFields.description, issue.getDescription());
        task.setValue(AllFields.taskStatus, issue.getStatus().getName());
        var dueDate = issue.getDueDate();
        if (dueDate != null) {
            task.setValue(AllFields.dueDate, dueDate.toDate());
        }
        var createdOn = issue.getCreationDate();
        if (createdOn != null) {
            task.setValue(AllFields.createdOn, createdOn.toDate());
        }
        // TODO set Done Ratio
        // task.setDoneRatio(issue.getDoneRatio());
        var jiraPriorityName = issue.getPriority() != null ? issue.getPriority().getName() : null;

        var priorityValue = priorities.getPriorityByText(jiraPriorityName);
        task.setValue(AllFields.priority, priorityValue);
        var timeTracking = issue.getTimeTracking();
        if (timeTracking != null) {
            var originalEstimateMinutes = timeTracking.getOriginalEstimateMinutes();
            if (originalEstimateMinutes != null && !(originalEstimateMinutes == 0)) {
                task.setValue(AllFields.estimatedTime, originalEstimateMinutes / 60F);
            }

//      var spentTimeMinutes = timeTracking.getTimeSpentMinutes
//      task.setValue(SpentTime, (spentTimeMinutes/60.0).toFloat)
        }
        JiraToGTaskHelper.processCustomFields(customFieldResolver, issue, task);
        processRelations(issue, task);
        processParentTask(issue, task);
        return task;
    }

    private static void processParentTask(Issue issue, GTask task) {
        if (issue.getIssueType().isSubtask()) {
            var parent = issue.getField("parent").getValue();
            var json = (JSONObject) parent;
            try {
                var parentKey = json.getString("key");
                var id = json.getLong("id");
                task.setParentIdentity(new TaskId(id, parentKey));
            } catch (JSONException e) {
                logger.error("error while parsing jira data" + e.toString(), e);
            }
        }
    }

    private static void processRelations(Issue issue, GTask genericTask) {
        var links = issue.getIssueLinks();
        if (links != null) {

            for (IssueLink link : links) {
                if (link.getIssueLinkType().getDirection() == IssueLinkType.Direction.OUTBOUND) {
                    var name = link.getIssueLinkType().getName();
                    if (name.equals(JiraConstants.getJiraLinkNameForPrecedes())) {
                        // targetIssueIdFromURI = JiraUtils.getIdFromURI(link.getTargetIssueUri());
                        var r = new GRelation(new TaskId(issue.getId(), issue.getKey()),
                                new TaskId(-1L, link.getTargetIssueKey()), GRelationType.precedes);
                        genericTask.getRelations().add(r);
                    } else {
                        logger.info("Relation type is not supported: " + link.getIssueLinkType()
                                + " - this link will be skipped for issue " + issue.getKey());
                    }
                }
            }
        }
    }

}
