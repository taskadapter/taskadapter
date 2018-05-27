package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AssigneeFullName$;
import com.taskadapter.model.AssigneeLoginName$;
import com.taskadapter.model.CreatedOn$;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Description$;
import com.taskadapter.model.DoneRatio$;
import com.taskadapter.model.DueDate$;
import com.taskadapter.model.EstimatedTime$;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Precedes$;
import com.taskadapter.model.Priority$;
import com.taskadapter.model.ReporterFullName$;
import com.taskadapter.model.ReporterLoginName$;
import com.taskadapter.model.StartDate$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.TargetVersion$;
import com.taskadapter.model.TaskStatus$;
import com.taskadapter.model.TaskType$;
import com.taskadapter.model.UpdatedOn$;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.JavaConverters$;

import java.util.Arrays;
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
            Option<User> userWithPatchedLoginName = userCache.findRedmineUserByFullName(issue.getAssigneeName());
            if (userWithPatchedLoginName.isDefined()) {
                task.setValue(AssigneeLoginName$.MODULE$, userWithPatchedLoginName.get().getLogin());
            }
            task.setValue(AssigneeFullName$.MODULE$, issue.getAssigneeName());
        }
        if (issue.getAuthorId() != null) {
            task.setValue(ReporterFullName$.MODULE$, issue.getAuthorName());
            // crappy Redmine REST API does not return login name, only id and "display name",
            // this Redmine Java API library can only provide that info... have to resolve login from full name -
            Option<User> userWithPatchedLoginName = userCache.findRedmineUserByFullName(issue.getAuthorName());
            if (userWithPatchedLoginName.isDefined()) {
                task.setValue(ReporterLoginName$.MODULE$, userWithPatchedLoginName.get().getLogin());
            }
        }

        Tracker tracker = issue.getTracker();
        if (tracker != null) {
            task.setValue(TaskType$.MODULE$, tracker.getName());
        }
        if (issue.getCategory() != null) {
            task.setValue(RedmineField$.MODULE$.category(),
                    JavaConverters$.MODULE$.asScalaBuffer(Arrays.asList(issue.getCategory().getName())));
        }
        task.setValue(TaskStatus$.MODULE$, issue.getStatusName());
        task.setValue(Summary$.MODULE$, issue.getSubject());
        task.setValue(EstimatedTime$.MODULE$, issue.getEstimatedHours());
        task.setValue(DoneRatio$.MODULE$, issue.getDoneRatio());
        task.setValue(StartDate$.MODULE$, issue.getStartDate());
        task.setValue(DueDate$.MODULE$, issue.getDueDate());
        task.setValue(CreatedOn$.MODULE$, issue.getCreatedOn());
        task.setValue(UpdatedOn$.MODULE$, issue.getUpdatedOn());
        Integer priorityValue = config.getPriorities().getPriorityByText(issue.getPriorityText());
        task.setValue(Priority$.MODULE$, priorityValue);
        task.setValue(Description$.MODULE$, issue.getDescription());
        if (issue.getTargetVersion() != null) {
            task.setValue(TargetVersion$.MODULE$, issue.getTargetVersion().getName());
        }
        processCustomFields(issue, task);
        processRelations(issue, task);
        return task;
    }

    private void processCustomFields(Issue issue, GTask task) {
        for (CustomField customField : issue.getCustomFields()) {
            task.setValue(new CustomString(customField.getName()), customField.getValue());
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
