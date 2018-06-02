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
      val resolver = JiraClientHelper.loadCustomFields(client)
      val jql = if (config.getQueryId != null) {
        JqlBuilder.findIssuesByProjectAndFilterId(config.getProjectKey, config.getQueryId)
      } else {
        JqlBuilder.findIssuesByProject(config.getProjectKey)
      }
      val issues = JiraClientHelper.findIssues(client, jql)
      val rows = jiraToGTask.convertToGenericTaskList(resolver, issues.asScala)
      rows.asScala
    } catch {
      case e: Exception =>
        throw JiraUtils.convertException(e)
    }
  }

  def loadTask(taskKey: String): GTask = {
    val resolver = JiraClientHelper.loadCustomFields(client)
    val promise = client.getIssueClient.getIssue(taskKey)
    val issue = promise.claim
    jiraToGTask.convertToGenericTask(resolver, issue)
  }
}
