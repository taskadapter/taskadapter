package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.connector.common.TaskSavingUtils
import com.taskadapter.connector.definition._
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask
import com.taskadapter.redmineapi.{Include, RedmineException, RedmineManager}
import com.taskadapter.redmineapi.bean._

import scala.collection.JavaConverters._

object RedmineConnector {
  /**
    * Keep it the same to enable backward compatibility for previously created config files.
    */
  val ID = "Redmine"

  @throws[RedmineException]
  private def loadPriorities(rows: java.lang.Iterable[FieldRow], mgr: RedmineManager): util.Map[String, Integer] = {
    if (FieldRowFinder.containsTargetField(rows.asScala.toSeq, RedmineField.priority.name))
      loadPriorities(mgr)
    else
      new util.HashMap[String, Integer]
  }

  @throws[RedmineException]
  private def loadPriorities(mgr: RedmineManager): util.Map[String, Integer] = {
    mgr.getIssueManager.getIssuePriorities.asScala.map(p => (p.getName, p.getId)).toMap.asJava
  }
}

class RedmineConnector(config: RedmineConfig, setup: WebConnectorSetup) extends NewConnector {
  override def loadTaskByKey(id: TaskId, rows: java.lang.Iterable[FieldRow]): GTask = {
    val httpClient = RedmineManagerFactory.createRedmineHttpClient()
    try {
      val mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient)
      val intKey = id.id.toInt
      val issue = mgr.getIssueManager.getIssueById(intKey, Include.relations)
      val converter = new RedmineToGTask(config)
      converter.convertToGenericTask(issue)
    } catch {
      case e: RedmineException =>
        throw new RuntimeException(e)
    } finally httpClient.getConnectionManager.shutdown()
  }

  override def loadData(): util.List[GTask] = {
    val httpClient = RedmineManagerFactory.createRedmineHttpClient()
    try {
      val mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient)

      val issues = mgr.getIssueManager.getIssues(config.getProjectKey, config.getQueryId, Include.relations)
      addFullUsers(issues, mgr)
      convertToGenericTasks(config, issues)
    } catch {
      case e: RedmineException =>
        throw new RuntimeException(e)
    } finally httpClient.getConnectionManager.shutdown()
  }

  @throws[RedmineException]
  private def addFullUsers(issues: util.List[Issue], mgr: RedmineManager) = {
    val users = new util.HashMap[Integer, User]
    issues.asScala.foreach { issue =>
      issue.setAssigneeName(patchUserDisplayName(issue.getAssigneeId, users, mgr))
      issue.setAuthorName(patchUserDisplayName(issue.getAuthorId, users, mgr))
    }
  }

  @throws[RedmineException]
  private def patchUserDisplayName(userId: Integer, users: util.Map[Integer, User], mgr: RedmineManager): String = {
    if (userId == null) return null

    val guess = users.get(userId)
    if (guess != null) return guess.getLogin

    val loaded = mgr.getUserManager.getUserById(userId)
    users.put(userId, loaded)
    loaded.getFullName
  }

  private def convertToGenericTasks(config: RedmineConfig, issues: util.List[Issue]) = {
    val converter = new RedmineToGTask(config)
    issues.asScala.map( i => converter.convertToGenericTask(i)).asJava
  }

  override def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask],
                        monitor: ProgressMonitor,
                        fieldRows: Iterable[FieldRow]): SaveResult = try {
    val httpClient = RedmineManagerFactory.createRedmineHttpClient()
    val mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient)
    try {
      val rmProject = mgr.getProjectManager.getProjectByKey(config.getProjectKey)
      val priorities = RedmineConnector.loadPriorities(fieldRows.asJava, mgr)
      val users = if (!config.isFindUserByName) new util.ArrayList[User]
      else mgr.getUserManager.getUsers
      val statusList = mgr.getIssueManager.getStatuses
      val versions = mgr.getProjectManager.getVersions(rmProject.getId)
      val customFieldDefinitions = mgr.getCustomFieldManager.getCustomFieldDefinitions
      val converter = new GTaskToRedmine(config, priorities, rmProject, users, customFieldDefinitions, statusList, versions)
      val saver = new RedmineTaskSaver(mgr.getIssueManager, config)
      val tsrb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, fieldRows,
        setup.host)
      TaskSavingUtils.saveRemappedRelations(config, tasks, saver, tsrb)
      tsrb.getResult
    } finally httpClient.getConnectionManager.shutdown()
  } catch {
    case e: RedmineException =>
      throw new RuntimeException(e)
  }
}
