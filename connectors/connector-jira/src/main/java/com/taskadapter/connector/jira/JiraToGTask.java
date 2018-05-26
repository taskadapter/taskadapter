package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.taskadapter.model.Assignee$;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.Components$;
import com.taskadapter.model.CreatedOn$;
import com.taskadapter.model.Description$;
import com.taskadapter.model.DueDate$;
import com.taskadapter.model.EstimatedTime$;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import com.taskadapter.model.Precedes$;
import com.taskadapter.model.Priority$;
import com.taskadapter.model.Reporter$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.TaskStatus$;
import com.taskadapter.model.TaskType$;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConverters$;

import java.util.ArrayList;
import java.util.List;

public class JiraToGTask {
    private static final Logger logger = LoggerFactory.getLogger(JiraToGTask.class);

    private final Priorities priorities;

    public JiraToGTask(Priorities priorities) {
        this.priorities = priorities;
    }

    public List<GTask> convertToGenericTaskList(CustomFieldResolver customFieldResolver, Iterable<Issue> issues) {
        // TODO see http://jira.atlassian.com/browse/JRA-6896
//        logger.info("Jira: no tasks hierarchy is supported");

        List<GTask> rootLevelTasks = new ArrayList<>();

        for (Issue issue : issues) {
            GTask genericTask = convertToGenericTask(customFieldResolver, issue);
            rootLevelTasks.add(genericTask);
        }
        return rootLevelTasks;
    }

    public GTask convertToGenericTask(CustomFieldResolver customFieldResolver, Issue issue) {
        GTask task = new GTask();
        final Long longId = issue.getId();
        task.setId(longId);
        task.setKey(issue.getKey());
        // must set source system id, otherwise "update task" is impossible later
        task.setSourceSystemId(new TaskId(longId, issue.getKey()));

        List<String> target = new ArrayList<>();
        issue.getComponents().forEach(c -> target.add(c.getName()));

        task.setValue(Components$.MODULE$, JavaConverters$.MODULE$.asScalaBuffer(target));
        if (issue.getAssignee() != null) {
            GUser user = new GUser(null, issue.getAssignee().getName(), issue.getAssignee().getDisplayName());
            task.setValue(Assignee$.MODULE$, user);
        }
        if (issue.getReporter() != null) {
            GUser user = new GUser(null, issue.getReporter().getName(), issue.getReporter().getDisplayName());
            task.setValue(Reporter$.MODULE$, user);
        }

        task.setValue(TaskType$.MODULE$, issue.getIssueType().getName());
        task.setValue(Summary$.MODULE$, issue.getSummary());
        task.setValue(Description$.MODULE$, issue.getDescription());
        task.setValue(TaskStatus$.MODULE$, issue.getStatus().getName());

        DateTime dueDate = issue.getDueDate();
        if (dueDate != null) {
            task.setValue(DueDate$.MODULE$, dueDate.toDate());
        }

        DateTime createdOn = issue.getCreationDate();
        if (createdOn != null) {
            task.setValue(CreatedOn$.MODULE$, createdOn.toDate());
        }

        // TODO set Done Ratio
        // task.setDoneRatio(issue.getDoneRatio());

        String jiraPriorityName = null;
        if (issue.getPriority() != null) {
            jiraPriorityName = issue.getPriority().getName();
        }
        Integer priorityValue = priorities.getPriorityByText(jiraPriorityName);
        task.setValue(Priority$.MODULE$, priorityValue);

        TimeTracking timeTracking = issue.getTimeTracking();
        if (timeTracking != null) {
            Integer originalEstimateMinutes = timeTracking.getOriginalEstimateMinutes();
            if (originalEstimateMinutes != null
                    && !originalEstimateMinutes.equals(0)) {
                task.setValue(EstimatedTime$.MODULE$, (float) (originalEstimateMinutes / 60.0));
            }
        }

        JiraToGTaskHelper$.MODULE$.processCustomFields(customFieldResolver, issue, task);
        processRelations(issue, task);
        processParentTask(issue, task);
        return task;
    }

    private static void processParentTask(Issue issue, GTask task) {
        if (issue.getIssueType().isSubtask()) {
            Object parent = issue.getField("parent").getValue();
            JSONObject json = (JSONObject) parent;
            try {
                String parentKey = (String) json.get("key");
                Long id = json.getLong("id");
                task.setParentIdentity(new TaskId(id, parentKey));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void processRelations(Issue issue, GTask genericTask) {
        Iterable<IssueLink> links = issue.getIssueLinks();
        if (links != null) {
            for (IssueLink link : links) {
                if (link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.OUTBOUND)) {
                    String name = link.getIssueLinkType().getName();
                    if (name.equals(JiraConstants.getJiraLinkNameForPrecedes())) {
//                        String targetIssueIdFromURI = JiraUtils.getIdFromURI(link.getTargetIssueUri());
                        GRelation r = new GRelation(
                                new TaskId(issue.getId(), issue.getKey()),
                                new TaskId(-1, link.getTargetIssueKey()),
                                Precedes$.MODULE$);
                        genericTask.getRelations().add(r);
                    } else {
                        logger.info("Relation type is not supported: " + link.getIssueLinkType() +
                                " - this link will be skipped for issue " + issue.getKey());
                    }
                }
            }
        }
    }

}
