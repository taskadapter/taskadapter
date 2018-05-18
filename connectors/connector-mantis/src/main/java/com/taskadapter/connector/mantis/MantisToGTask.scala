package com.taskadapter.connector.mantis

import biz.futureware.mantis.rpc.soap.client.{AccountData, IssueData}
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._
import org.slf4j.LoggerFactory

object MantisToGTask {
  private val logger = LoggerFactory.getLogger(MantisToGTask.getClass)
  // TODO this can be moved to properties section to be defined by user.

  private val defaultPriority = 500

  private val priorityNumbers = Map[String, Int](
    "none" -> 100,
    "low" -> 100,
    "normal" -> defaultPriority,
    "high" -> 700,
    "urgent" -> 800,
    "immediate" -> 800
  )

  def convertToGUser(mantisUser: AccountData): GUser = {
    GUser(mantisUser.getId.intValue, mantisUser.getName, mantisUser.getReal_name)
  }

  def convertToGenericTask(issue: IssueData): GTask = {
    val task = new GTask
    val longId = issue.getId.longValue
    task.setId(longId)
    val stringId = String.valueOf(issue.getId)
    task.setKey(stringId)
    // must set source system id, otherwise "update task" is impossible later
    task.setSourceSystemId(TaskId(longId, stringId))

    val mantisUser = issue.getHandler
    if (mantisUser != null) {
      val ass = convertToGUser(mantisUser)
      task.setValue(Assignee, ass)
    }
    task.setValue(Summary, issue.getSummary)
    task.setValue(Description, issue.getDescription)
    task.setValue(CreatedOn, issue.getDate_submitted.getTime)
    task.setValue(UpdatedOn, issue.getLast_updated.getTime)
    val priorityValue: Int = priorityNumbers.getOrElse(issue.getPriority.getName, defaultPriority)
    task.setValue(Priority, priorityValue)
    if (issue.getDue_date != null) {
      task.setValue(DueDate, issue.getDue_date.getTime)
    }
    processRelations(issue, task)
    task
  }

  private def processRelations(mntIssue: IssueData, genericTask: GTask) = {
    val relations = mntIssue.getRelationships
    if (relations != null) {
      for (relation <- relations) {
        if (relation.getType.getName == "child of") {
          val r = GRelation(
            TaskId(relation.getId.longValue, String.valueOf(relation.getId)),
            TaskId(relation.getTarget_id.longValue, String.valueOf(relation.getTarget_id)),
            Precedes)
          genericTask.getRelations.add(r)
        } else {
          logger.info("Relation type is not supported: " + relation.getType + " - skipping it for issue " + mntIssue.getId)
        }
      }
    }
  }
}