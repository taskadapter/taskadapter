package com.taskadapter.connector.redmine

import java.util
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{AssigneeFullName, AssigneeLoginName, CreatedOn, CustomString, Description, DoneRatio, DueDate, EstimatedTime, GRelation, GTask, Priority, GRelationType, ReporterFullName, ReporterLoginName, StartDate, Summary, TargetVersion, TaskStatus, TaskType, UpdatedOn}
import com.taskadapter.redmineapi.bean.{Issue, IssueRelation}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters
import scala.collection.JavaConversions._

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
      if (userWithPatchedLoginName.isDefined) task.setValue(AssigneeLoginName, userWithPatchedLoginName.get.getLogin)
      task.setValue(AssigneeFullName, issue.getAssigneeName)
    }
    if (issue.getAuthorId != null) {
      task.setValue(ReporterFullName, issue.getAuthorName)
      // this Redmine Java API library can only provide that info... have to resolve login from full name -
      val userWithPatchedLoginName = userCache.findRedmineUserByFullName(issue.getAuthorName)
      if (userWithPatchedLoginName.isDefined) task.setValue(ReporterLoginName, userWithPatchedLoginName.get.getLogin)
    }
    val tracker = issue.getTracker
    if (tracker != null) task.setValue(TaskType, tracker.getName)
    if (issue.getCategory != null) {
      task.setValue(RedmineField.category,
        JavaConverters.asScalaBuffer(util.Arrays.asList(issue.getCategory.getName)))
    }
    task.setValue(TaskStatus, issue.getStatusName)
    task.setValue(Summary, issue.getSubject)
    task.setValue(EstimatedTime, issue.getEstimatedHours.toFloat)
//    task.setValue(SpentTime, issue.getSpentHours.toFloat)
    task.setValue(DoneRatio, java.lang.Float.valueOf(issue.getDoneRatio.toFloat))
    task.setValue(StartDate, issue.getStartDate)
    task.setValue(DueDate, issue.getDueDate)
    task.setValue(CreatedOn, issue.getCreatedOn)
    task.setValue(UpdatedOn, issue.getUpdatedOn)
    val priorityValue = config.getPriorities.getPriorityByText(issue.getPriorityText)
    task.setValue(Priority, priorityValue)
    task.setValue(Description, issue.getDescription)
    if (issue.getTargetVersion != null) task.setValue(TargetVersion, issue.getTargetVersion.getName)
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