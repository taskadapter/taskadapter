package com.taskadapter.connector.github

import java.io.IOException

import com.taskadapter.connector.common.BasicIssueSaveAPI
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.definition.exceptions.ConnectorException
import org.eclipse.egit.github.core.Issue
import org.eclipse.egit.github.core.service.IssueService

final class GithubTaskSaver(var issueService: IssueService, val userName: String, val projectKey: String) extends BasicIssueSaveAPI[Issue] {
  @throws[ConnectorException]
  override def createTask(issue: Issue): TaskId = {
    val repositoryName = projectKey
    try {
      val createdIssue = issueService.createIssue(userName, repositoryName, issue)
      TaskId(createdIssue.getNumber, createdIssue.getNumber + "")
    } catch {
      case e: IOException =>
        throw GithubUtils.convertException(e)
    }
  }

  @throws[ConnectorException]
  override def updateTask(issue: Issue): Unit = try issueService.editIssue(userName, projectKey, issue)
  catch {
    case e: IOException =>
      throw GithubUtils.convertException(e)
  }
}