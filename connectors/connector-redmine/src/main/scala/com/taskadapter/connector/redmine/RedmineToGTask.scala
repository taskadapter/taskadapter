package com.taskadapter.connector.redmine

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{AllFields, CustomString, GRelation, GRelationType, GTask}
import com.taskadapter.redmineapi.bean.{Issue, IssueRelation}
import org.slf4j.LoggerFactory

import java.util
import scala.collection.JavaConversions._
import scala.collection.JavaConverters

object RedmineToGTask {
  private val logger = LoggerFactory.getLogger(classOf[RedmineToGTask])

  def processRelations(rmIssue: Issue, genericTask: GTask) = {
    val relations = rmIssue.getRelations
    for (relation <- relations) {
      if (relation.getType == IssueRelation.TYPE.precedes.toString) { // if NOT equal to self!
        // See http://www.redmine.org/issues/7366#note-11
        if (!(relation.getIssueToId == rmIssue.getId)) {
          val r = new GRelation(new TaskId(rmIssue.getId.longValue(), rmIssue.getId + ""),
            new TaskId(relation.getIssueToId.longValue(), relation.getIssueToId + ""), GRelationType.precedes)
          genericTask.getRelations.add(r)
        }
      }
      else logger.info("Relation type is not supported: " + relation.getType + " - skipping it for issue " + rmIssue.getId)
    }
  }
}

class RedmineToGTask(val config: RedmineConfig, var userCache: RedmineUserCache) {
  /**
    * convert Redmine issues to internal model representation required for
    * Task Adapter app.
    *
    * @param issue Redmine issue
    */
  def convertToGenericTask(issue: Issue): GTask = {
    val task = new GTask
    task.setId(if (issue.getId == null) null
    else issue.getId.longValue)
    if (issue.getId != null) {
      val stringKey = Integer.toString(issue.getId)
      task.setKey(stringKey)
      task.setSourceSystemId(new TaskId(issue.getId.longValue(), stringKey))
    }
    if (issue.getParentId != null) task.setParentIdentity(new TaskId(issue.getParentId.toLong, issue.getParentId + ""))
    if (issue.getAssigneeId != null) { // crappy Redmine REST API does not return login name, only id and "display name",
      // this Redmine Java API library can only provide that info... this is why "loginName" is empty here.
      val userWithPatchedLoginName = userCache.findRedmineUserByFullName(issue.getAssigneeName)
      if (userWithPatchedLoginName.isDefined) task.setValue(AllFields.assigneeLoginName, userWithPatchedLoginName.get.getLogin)
      task.setValue(AllFields.assigneeFullName, issue.getAssigneeName)
    }
    if (issue.getAuthorId != null) {
      task.setValue(AllFields.reporterFullName, issue.getAuthorName)
      // this Redmine Java API library can only provide that info... have to resolve login from full name -
      val userWithPatchedLoginName = userCache.findRedmineUserByFullName(issue.getAuthorName)
      if (userWithPatchedLoginName.isDefined) task.setValue(AllFields.reporterLoginName, userWithPatchedLoginName.get.getLogin)
    }
    val tracker = issue.getTracker
    if (tracker != null) task.setValue(AllFields.taskType, tracker.getName)
    if (issue.getCategory != null) {
      task.setValue(RedmineField.category,
        util.Arrays.asList(issue.getCategory.getName))
    }
    task.setValue(AllFields.taskStatus, issue.getStatusName)
    task.setValue(AllFields.summary, issue.getSubject)
    task.setValue(AllFields.estimatedTime, issue.getEstimatedHours)
//    task.setValue(SpentTime, issue.getSpentHours.toFloat)
    task.setValue(AllFields.doneRatio, java.lang.Float.valueOf(issue.getDoneRatio.toFloat))
    task.setValue(AllFields.startDate, issue.getStartDate)
    task.setValue(AllFields.dueDate, issue.getDueDate)
    task.setValue(AllFields.createdOn, issue.getCreatedOn)
    task.setValue(AllFields.updatedOn, issue.getUpdatedOn)
    val priorityValue = config.getPriorities.getPriorityByText(issue.getPriorityText)
    task.setValue(AllFields.priority, priorityValue)
    task.setValue(AllFields.description, issue.getDescription)
    if (issue.getTargetVersion != null) {
      task.setValue(AllFields.targetVersion, issue.getTargetVersion.getName)
    }
    processCustomFields(issue, task)
    RedmineToGTask.processRelations(issue, task)
    task
  }

  private def processCustomFields(issue: Issue, task: GTask) = {
    for (customField <- issue.getCustomFields) {
      task.setValue(new CustomString(customField.getName), customField.getValue)
    }
  }
}