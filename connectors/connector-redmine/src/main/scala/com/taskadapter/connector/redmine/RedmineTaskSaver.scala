package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.connector.common.{BasicIssueSaveAPI, RelationSaver}
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.definition.exceptions.{CommunicationException, ConnectorException}
import com.taskadapter.model.GRelation
import com.taskadapter.redmineapi.{IssueManager, RedmineException, RedmineProcessingException}
import com.taskadapter.redmineapi.bean.Issue
import scala.collection.JavaConverters._

final class RedmineTaskSaver(val issueManager: IssueManager, val config: RedmineConfig) extends RelationSaver with BasicIssueSaveAPI[Issue] {
  @throws[ConnectorException]
  override def createTask(nativeTask: Issue): TaskId = try {
    val newIssue = issueManager.createIssue(nativeTask)
    TaskId(newIssue.getId.longValue, newIssue.getId.toString)
  } catch {
    case e: RedmineException =>
      throw RedmineExceptions.convertException(e)
  }

  @throws[ConnectorException]
  override def updateTask(rmIssue: Issue): Unit = try {
    issueManager.update(rmIssue)
    // TODO this is here and not in saveRelations() because it needs issue ID to delete the old relations for
    if (config.getSaveIssueRelations) issueManager.deleteIssueRelationsByIssueId(rmIssue.getId)
  } catch {
    case e: RedmineException =>
      throw RedmineExceptions.convertException(e)
  }

  @throws[ConnectorException]
  override def saveRelations(relations: util.List[GRelation]): Unit = try {
    for (gRelation <- relations.asScala) {
      val taskId = gRelation.taskId
      val intTaskId = taskId.id.toInt
      val relatedTaskKey = gRelation.relatedTaskId
      val intRelatedId = relatedTaskKey.id.toInt
      issueManager.createRelation(intTaskId, intRelatedId, gRelation.`type`.toString)
    }
  } catch {
    case e: RedmineProcessingException =>
      throw new RelationCreationException(e)
    case e: RedmineException =>
      throw new CommunicationException(e)
  }
}