package com.taskadapter.connector.redmine;

import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class RedmineToGTask {

    private static final Logger logger = LoggerFactory.getLogger(RedmineToGTask.class);

    private final RedmineConfig config;

    public RedmineToGTask(RedmineConfig config) {
        this.config = config;
    }

    /**
     * convert Redmine issues to internal model representation required for
     * Task Adapter app.
     *
     * @param issue Redmine issue
     */
    public GTask convertToGenericTask(Issue issue) {
        GTask task = new GTask();

        task.setId(issue.getId());
        if (issue.getId() != null) {
            task.setKey(Integer.toString(issue.getId()));
        }
        if (issue.getParentId() != null) {
            task.setParentKey(issue.getParentId() + "");
        }
        User rmAss = issue.getAssignee();
        if (rmAss != null) {
            task.setValue(RedmineField.assignee(), RedmineToGUser.convertToGUser(rmAss));
        }

        Tracker tracker = issue.getTracker();
        if (tracker != null) {
            task.setValue(RedmineField.taskType(), tracker.getName());
        }
        task.setValue(RedmineField.taskStatus(), issue.getStatusName());
        task.setValue(RedmineField.summary(), issue.getSubject());
        task.setValue(RedmineField.estimatedTime(), issue.getEstimatedHours());
        task.setValue(RedmineField.doneRatio(), issue.getDoneRatio());
        task.setValue(RedmineField.startDate(), issue.getStartDate());
        task.setValue(RedmineField.dueDate(), issue.getDueDate());
        task.setValue(RedmineField.createdOn(), issue.getCreatedOn());
        task.setValue(RedmineField.updatedOn(), issue.getUpdatedOn());
        Integer priorityValue = config.getPriorities().getPriorityByText(issue.getPriorityText());
        task.setValue(RedmineField.priority(), priorityValue);
        task.setValue(RedmineField.description(), issue.getDescription());
        if (issue.getTargetVersion() != null) {
            task.setValue(RedmineField.targetVersion(), issue.getTargetVersion().getName());
        }
        processCustomFields(issue, task);
        processRelations(issue, task);
        return task;
    }

    private void processCustomFields(Issue issue, GTask task) {
        for (CustomField customField : issue.getCustomFields()) {
            task.setValue(customField.getName(), customField.getValue());
        }
    }

    private static void processRelations(Issue rmIssue, GTask genericTask) {
        Collection<IssueRelation> relations = rmIssue.getRelations();
        for (IssueRelation relation : relations) {
            if (relation.getType().equals(IssueRelation.TYPE.precedes.toString())) {
                // if NOT equal to self!
                // See http://www.redmine.org/issues/7366#note-11
                if (!relation.getIssueToId().equals(rmIssue.getId())) {
                    GRelation r = new GRelation(Integer.toString(rmIssue.getId()), Integer.toString(relation
                            .getIssueToId()), GRelation.TYPE.precedes);
                    genericTask.getRelations().add(r);
                }
            } else {
                logger.info("Relation type is not supported: " + relation.getType()
                        + " - skipping it for issue " + rmIssue.getId());
            }
        }
    }
}
