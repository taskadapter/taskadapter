package com.taskadapter.connector.redmine;

import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
            task.setAssignee(RedmineToGUser.convertToGUser(rmAss));
        }

        Tracker tracker = issue.getTracker();
        if (tracker != null) {
            task.setType(tracker.getName());
        }
        task.setStatus(issue.getStatusName());
        task.setSummary(issue.getSubject());
        task.setEstimatedHours(issue.getEstimatedHours());
        task.setDoneRatio(issue.getDoneRatio());
        task.setStartDate(issue.getStartDate());
        task.setDueDate(issue.getDueDate());
        task.setCreatedOn(issue.getCreatedOn());
        task.setUpdatedOn(issue.getUpdatedOn());
        Integer priorityValue = config.getPriorities().getPriorityByText(issue.getPriorityText());
        task.setPriority(priorityValue);
        task.setDescription(issue.getDescription());
        if (issue.getTargetVersion() != null) {
            task.setTargetVersionName(issue.getTargetVersion().getName());
        }

        processRelations(issue, task);
        return task;
    }

    private static void processRelations(Issue rmIssue, GTask genericTask) {
        List<IssueRelation> relations = rmIssue.getRelations();
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
                logger.error("relation type is not supported: " + relation.getType());
            }
        }
    }
}
