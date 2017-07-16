package com.taskadapter.connector.jira

import java.util

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput
import com.taskadapter.connector.common.{BasicIssueSaveAPI, RelationSaver}
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.{GRelation, Precedes}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class JiraTaskSaver(val client: JiraRestClient) extends RelationSaver with BasicIssueSaveAPI[IssueWrapper] {
  val logger = LoggerFactory.getLogger(classOf[JiraTaskSaver])

  @throws[ConnectorException]
  override def createTask(wrapper: IssueWrapper): TaskId = JiraClientHelper.createTask(client, wrapper.issueInput)

  @throws[ConnectorException]
  override def updateTask(wrapper: IssueWrapper): Unit = {
    val promise = client.getIssueClient.updateIssue(wrapper.key, wrapper.issueInput)
    promise.claim
  }

  @throws[ConnectorException]
  override def saveRelations(relations: util.List[GRelation]): Unit = {
    relations.asScala.foreach { relation =>
      val taskKey = relation.taskId.key
      val relatedTaskKey = relation.relatedTaskId.key

      if (relation.`type` == Precedes) {
        var linkTypeName = JiraConstants.getJiraLinkNameForPrecedes
        val input = new LinkIssuesInput(taskKey, relatedTaskKey, linkTypeName)
        val promise = client.getIssueClient.linkIssue(input)
        promise.claim
      } else {
        logger.info("Ignoring not supported issue link type: " + relation.`type` + ". JIRA connector only supports " + Precedes)
      }
    }
  }
}
