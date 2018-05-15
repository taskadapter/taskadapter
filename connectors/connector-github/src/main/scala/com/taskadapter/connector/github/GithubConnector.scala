package com.taskadapter.connector.github

import java.io.IOException
import java.util

import com.taskadapter.connector.common.TaskSavingUtils
import com.taskadapter.connector.definition._
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask
import org.eclipse.egit.github.core.service.IssueService


object GithubConnector {
  /**
    * Keep it the same to enable backward compatibility with the existing
    * config files.
    */
  val ID = "GitHub"
}

class GithubConnector(config: GithubConfig, setup: WebConnectorSetup) extends NewConnector {
  @throws[ConnectorException]
  override def loadTaskByKey(key: TaskId, rows: Iterable[FieldRow[_]]): GTask = {
    val issueService = new ConnectionFactory(setup).getIssueService
    try {
      val issue = issueService.getIssue(setup.userName, config.getProjectKey, key.id.toInt)
      GithubToGTask.toGtask(issue)
    } catch {
      case e: IOException =>
        throw GithubUtils.convertException(e)
    }
  }

  private def getIssueService = {
    val cf = new ConnectionFactory(setup)
    cf.getIssueService
  }

  @throws[ConnectorException]
  override def loadData(): util.List[GTask] = {
    val issuesFilter = new util.HashMap[String, String]
    issuesFilter.put(IssueService.FILTER_STATE, if (config.getIssueState == null) IssueService.STATE_OPEN
    else config.getIssueState)
    val issueService = getIssueService
    try {
      val issues = issueService.getIssues(setup.userName, config.getProjectKey, issuesFilter)
      GithubToGTask.toGTaskList(issues)
    } catch {
      case e: IOException =>
        throw GithubUtils.convertException(e)
    }
  }

  override def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask], monitor: ProgressMonitor,
                        rows: Iterable[FieldRow[_]]): SaveResult = {
    val ghConnector = new ConnectionFactory(setup)
    val converter = new GTaskToGithub(ghConnector.getUserService)
    val issueService = ghConnector.getIssueService
    val saver = new GithubTaskSaver(issueService, setup.userName, config.getProjectKey)
    val rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, rows,
      setup.host)
    rb.getResult
  }
}
