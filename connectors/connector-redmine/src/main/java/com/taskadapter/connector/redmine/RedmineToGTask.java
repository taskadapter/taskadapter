package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelationType;
import com.taskadapter.model.GTask;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RedmineToGTask {
    private static final Logger logger = LoggerFactory.getLogger(RedmineToGTask.class);

    private final RedmineConfig config;
    private final RedmineUserCache userCache;

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
        var task = new GTask();
        var id = issue.getId() == null ? null : issue.getId().longValue();
        task.setId(id);
        if (issue.getId() != null) {
            var stringKey = Integer.toString(issue.getId());
            task.setKey(stringKey);
            task.setSourceSystemId(new TaskId(issue.getId().longValue(), stringKey));
        }
        if (issue.getParentId() != null) {
            task.setParentIdentity(new TaskId(issue.getParentId().longValue(), issue.getParentId() + ""));
        }
        if (issue.getAssigneeId() != null) { // crappy Redmine REST API does not return login name, only id and "display name",
            // this Redmine Java API library can only provide that info... this is why "loginName" is empty here.
            var userWithPatchedLoginName = userCache.findRedmineUserByFullName(issue.getAssigneeName());
            if (userWithPatchedLoginName.isPresent()) {
                task.setValue(AllFields.assigneeLoginName, userWithPatchedLoginName.get().getLogin());
            }
            task.setValue(AllFields.assigneeFullName, issue.getAssigneeName());
        }
        if (issue.getAuthorId() != null) {
            task.setValue(AllFields.reporterFullName, issue.getAuthorName());
            // this Redmine Java API library can only provide that info... have to resolve login from full name -
            var userWithPatchedLoginName = userCache.findRedmineUserByFullName(issue.getAuthorName());
            if (userWithPatchedLoginName.isPresent()) {
                task.setValue(AllFields.reporterLoginName, userWithPatchedLoginName.get().getLogin());
            }
        }
        var tracker = issue.getTracker();
        if (tracker != null) task.setValue(AllFields.taskType, tracker.getName());
        if (issue.getCategory() != null) {
            task.setValue(RedmineField.category,
                    Arrays.asList(issue.getCategory().getName()));
        }
        task.setValue(AllFields.taskStatus, issue.getStatusName());
        task.setValue(AllFields.summary, issue.getSubject());
        task.setValue(AllFields.estimatedTime, issue.getEstimatedHours());
//    task.setValue(SpentTime, issue.getSpentHours.toFloat);
        task.setValue(AllFields.doneRatio, issue.getDoneRatio() == null ? null : (float) issue.getDoneRatio());
        task.setValue(AllFields.startDate, issue.getStartDate());
        task.setValue(AllFields.dueDate, issue.getDueDate());
        task.setValue(AllFields.createdOn, issue.getCreatedOn());
        task.setValue(AllFields.updatedOn, issue.getUpdatedOn());
        var priorityValue = config.getPriorities().getPriorityByText(issue.getPriorityText());
        task.setValue(AllFields.priority, priorityValue);
        task.setValue(AllFields.description, issue.getDescription());
        if (issue.getTargetVersion() != null) {
            task.setValue(AllFields.targetVersion, issue.getTargetVersion().getName());
        }
        processCustomFields(issue, task);
        RedmineToGTask.processRelations(issue, task);
        return task;
    }

    private static void processCustomFields(Issue issue, GTask task) {
        for (CustomField customField : issue.getCustomFields()) {
            task.setValue(new CustomString(customField.getName()), customField.getValue());
        }
    }

    private static void processRelations(Issue rmIssue, GTask genericTask) {
        var relations = rmIssue.getRelations();
        for (IssueRelation relation : relations) {
            if (relation.getType().equals(IssueRelation.TYPE.precedes.toString())) { // if NOT equal to self!
                // See http://www.redmine.org/issues/7366#note-11
                if (!(relation.getIssueToId().equals(rmIssue.getId()))) {
                    var r = new GRelation(new TaskId(rmIssue.getId().longValue(), rmIssue.getId() + ""),
                            new TaskId(relation.getIssueToId().longValue(), relation.getIssueToId() + ""),
                            GRelationType.precedes);
                    genericTask.getRelations().add(r);
                }
            } else {
                logger.info("Relation type is not supported: " + relation.getType()
                        + " - skipping it for issue " + rmIssue.getId());
            }
        }
    }

}
