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
    if (FieldRowFinder.containsTargetField(rows, RedmineField.priority.name))
      loadPriorities(mgr)
    else
      new util.HashMap[String, Integer]
  }

  @throws[RedmineException]
  private def loadPriorities(mgr: RedmineManager): util.Map[String, Integer] = {
    mgr.getIssueManager.getIssuePriorities.asScala.map(p => (p.getName, p.getId)).toMap.asJava
  }
}

class RedmineConnector(var config: RedmineConfig, var serverInfo: WebServerInfo) extends NewConnector {
  override def loadTaskByKey(key: TaskId, rows: java.lang.Iterable[FieldRow]): GTask = try {
    val mgr = RedmineManagerFactory.createRedmineManager(serverInfo)
    val intKey = key.id.toInt
    val issue = mgr.getIssueManager.getIssueById(intKey, Include.relations)
    val converter = new RedmineToGTask(config)
    converter.convertToGenericTask(issue)
  } catch {
    case e: RedmineException =>
      throw new RuntimeException(e)
  }

  override def loadData(): util.List[GTask] = try {
    val mgr = RedmineManagerFactory.createRedmineManager(serverInfo)
    val issues = mgr.getIssueManager.getIssues(config.getProjectKey, config.getQueryId, Include.relations)
    addFullUsers(issues, mgr)
    convertToGenericTasks(config, issues)
  } catch {
    case e: RedmineException =>
      throw new RuntimeException(e)
  }

  @throws[RedmineException]
  private def addFullUsers(issues: util.List[Issue], mgr: RedmineManager) = {
    val users = new util.HashMap[Integer, User]
    issues.asScala.foreach { issue =>
      issue.setAssignee(patchAssignee(issue.getAssignee, users, mgr))
      issue.setAuthor(patchAssignee(issue.getAuthor, users, mgr))
    }
  }

  @throws[RedmineException]
  private def patchAssignee(user: User, users: util.Map[Integer, User], mgr: RedmineManager): User = {
    if (user == null) return null

    val guess = users.get(user.getId)
    if (guess != null) return guess

    val loaded = mgr.getUserManager.getUserById(user.getId)
    users.put(user.getId, loaded)
    loaded
  }

  private def convertToGenericTasks(config: RedmineConfig, issues: util.List[Issue]) = {
    val converter = new RedmineToGTask(config)
    issues.asScala.map( i => converter.convertToGenericTask(i)).asJava
  }

  override def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask],
                        monitor: ProgressMonitor,
                        fieldRows: java.lang.Iterable[FieldRow]): SaveResult = try {
    val mgr = RedmineManagerFactory.createRedmineManager(serverInfo)
    try {
      val rmProject = mgr.getProjectManager.getProjectByKey(config.getProjectKey)
      val priorities = RedmineConnector.loadPriorities(fieldRows, mgr)
      val users = if (!config.isFindUserByName) new util.ArrayList[User]
      else mgr.getUserManager.getUsers
      val statusList = mgr.getIssueManager.getStatuses
      val versions = mgr.getProjectManager.getVersions(rmProject.getId)
      val customFieldDefinitions = mgr.getCustomFieldManager.getCustomFieldDefinitions
      val converter = new GTaskToRedmine(config, priorities, rmProject, users, customFieldDefinitions, statusList, versions)
      val saver = new RedmineTaskSaver(mgr.getIssueManager, config)
      val tsrb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, fieldRows)
      TaskSavingUtils.saveRemappedRelations(config, tasks, saver, tsrb)
      tsrb.getResult
    } finally mgr.shutdown()
  } catch {
    case e: RedmineException =>
      throw new RuntimeException(e)
  }
}
