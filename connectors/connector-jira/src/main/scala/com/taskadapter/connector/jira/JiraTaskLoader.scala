package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.taskadapter.connector.Priorities
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.GTask

import scala.collection.JavaConverters._

class JiraTaskLoader(val client: JiraRestClient, val priorities: Priorities) {
  val jiraToGTask = new JiraToGTask(priorities)

  @throws[ConnectorException]
  def loadTasks(config: JiraConfig): Seq[GTask] = {
    try {
      val jql = if (config.getQueryId != null) {
        JqlBuilder.findIssuesByProjectAndFilterId(config.getProjectKey, config.getQueryId)
      } else {
        JqlBuilder.findIssuesByProject(config.getProjectKey)
      }
      val issues = JiraClientHelper.findIssues(client, jql)
      val fields = client.getMetadataClient.getFields
      val fieldIterable = fields.claim.asScala
      val rows = jiraToGTask.convertToGenericTaskList(issues)
      //      val userConverter = new JiraUserConverter(client)
      //            rows = userConverter.convertAssignees(rows);
      rows.asScala
    } catch {
      case e: Exception =>
        throw JiraUtils.convertException(e)
    }
  }

  def loadTask(taskKey: String): GTask = {
    val promise = client.getIssueClient.getIssue(taskKey)
    val issue = promise.claim
    jiraToGTask.convertToGenericTask(issue)
  }
}
