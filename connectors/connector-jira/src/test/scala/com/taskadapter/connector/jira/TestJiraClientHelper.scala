package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.taskadapter.connector.definition.TaskId

object TestJiraClientHelper {
  def findIssuesBySummary(client: JiraRestClient, summary: String) = {
    val jql = "summary~\"" + summary + "\""
    JiraClientHelper.findIssues(client, jql)
  }

  def deleteTasks(client: JiraRestClient, ids: TaskId*): Unit = {
    for (id <- ids) {
      val promise = client.getIssueClient.deleteIssue(id.key, true)
      promise.claim
    }
  }
}
