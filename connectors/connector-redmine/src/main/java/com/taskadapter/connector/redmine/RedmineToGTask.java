package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import com.taskadapter.model.Precedes$;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.util.Collection;

public class RedmineToGTask {

    private static final Logger logger = LoggerFactory.getLogger(RedmineToGTask.class);

    private final RedmineConfig config;
    private RedmineUserCache userCache;

    public RedmineToGTask(RedmineConfig config, RedmineUserCache userCache) {
        this.config = config;
        this.userCache = userCache;
    }

    /**
     * convert Redmine issues to internal model representation required for
     * Task Adapter app.
     *
     * @param issue Redmine issue
     */
    public GTask convertToGenericTask(Issue issue) {
        GTask task = new GTask();

        task.setId(issue.getId() == null? null : issue.getId().longValue());
        if (issue.getId() != null) {
            String stringKey = Integer.toString(issue.getId());
            task.setKey(stringKey);
            task.setSourceSystemId(new TaskId(issue.getId(), stringKey));
        }
        if (issue.getParentId() != null) {
            task.setParentIdentity(new TaskId(issue.getParentId(), issue.getParentId() + ""));
        }

        if (issue.getAssigneeId() != null) {
            // crappy Redmine REST API does not return login name, only id and "display name",
            // this Redmine Java API library can only provide that info... this is why "loginName" is empty here.
            Option<GUser> userWithPatchedLoginName = userCache.findGUserInCache(null, issue.getAssigneeName());
            if (userWithPatchedLoginName.isDefined()) {
                task.setValue(RedmineField.assignee(), userWithPatchedLoginName.get());
            } else {
                task.setValue(RedmineField.assignee(), new GUser(issue.getAssigneeId(), "", issue.getAssigneeName()));
            }
        }
        if (issue.getAuthorId() != null) {
            // crappy Redmine REST API does not return login name, only id and "display name",
            // this Redmine Java API library can only provide that info... this is why "loginName" is empty here.
            GUser user = new GUser(issue.getAuthorId(), "", issue.getAuthorName());
            task.setValue(RedmineField.author(), user);
        }

        Tracker tracker = issue.getTracker();
        if (tracker != null) {
            task.setValue(RedmineField.taskType(), tracker.getName());
        }
        if (issue.getCategory() != null) {
            task.setValue(RedmineField.category(), issue.getCategory().getName());
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
                    GRelation r = new GRelation(
                            new TaskId(rmIssue.getId(), rmIssue.getId()+""),
                            new TaskId(relation.getIssueToId(), relation.getIssueToId()+""),
                            Precedes$.MODULE$);
                    genericTask.getRelations().add(r);
                }
            } else {
                logger.info("Relation type is not supported: " + relation.getType()
                        + " - skipping it for issue " + rmIssue.getId());
            }
        }
    }
}
