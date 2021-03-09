package com.taskadapter.connector.mantis

import biz.futureware.mantis.rpc.soap.client.IssueData
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{GRelationType, _}
import org.slf4j.LoggerFactory

object MantisToGTask {
  private val logger = LoggerFactory.getLogger(MantisToGTask.getClass)
  // TODO this can be moved to properties section to be defined by user.

  private val defaultPriority : Integer = 500

  private val priorityNumbers = Map[String, Integer](
    "none" -> 100,
    "low" -> 100,
    "normal" -> defaultPriority,
    "high" -> 700,
    "urgent" -> 800,
    "immediate" -> 800
  )

  def convertToGenericTask(issue: IssueData): GTask = {
    val task = new GTask
    val longId = issue.getId.longValue
    task.setId(longId)
    val stringId = String.valueOf(issue.getId)
    task.setKey(stringId)
    // must set source system id, otherwise "update task" is impossible later
    task.setSourceSystemId(new TaskId(longId, stringId))

    val mantisUser = issue.getHandler
    if (mantisUser != null) {
      task.setValue(AllFields.assigneeLoginName, mantisUser.getName)
      task.setValue(AllFields.assigneeFullName, mantisUser.getReal_name)
    }
    task. setValue(AllFields.summary, issue.getSummary)
    task.setValue(AllFields.description, issue.getDescription)
    task.setValue(AllFields.createdOn, issue.getDate_submitted.getTime)
    task.setValue(AllFields.updatedOn, issue.getLast_updated.getTime)
    val priorityValue: Integer = priorityNumbers.getOrElse(issue.getPriority.getName, defaultPriority)
    task.setValue(AllFields.priority, priorityValue)
    if (issue.getDue_date != null) {
      task.setValue(AllFields.dueDate, issue.getDue_date.getTime)
    }
    processRelations(issue, task)
    task
  }

  private def processRelations(mntIssue: IssueData, genericTask: GTask) = {
    val relations = mntIssue.getRelationships
    if (relations != null) {
      for (relation <- relations) {
        if (relation.getType.getName == "child of") {
          val r = new GRelation(
            new TaskId(relation.getId.longValue, String.valueOf(relation.getId)),
            new TaskId(relation.getTarget_id.longValue, String.valueOf(relation.getTarget_id)),
            GRelationType.precedes)
          genericTask.getRelations.add(r)
        } else {
          logger.info("Relation type is not supported: " + relation.getType + " - skipping it for issue " + mntIssue.getId)
        }
      }
    }
  }
}