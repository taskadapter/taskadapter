package com.taskadapter.connector.mantis

import java.math.BigInteger
import java.rmi.RemoteException
import java.util

import biz.futureware.mantis.rpc.soap.client.{AccountData, IssueData}
import com.taskadapter.connector.common.TaskSavingUtils
import com.taskadapter.connector.definition._
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask

object MantisConnector {
  val ID = "Mantis"
}

class MantisConnector(config: MantisConfig, setup: WebConnectorSetup) extends NewConnector {
  override def loadTaskByKey(key: TaskId, rows: Iterable[FieldRow[_]]): GTask = {
    val mgr = MantisManagerFactory.createMantisManager(setup)
    try {
      val issue = mgr.getIssueById(BigInteger.valueOf(key.id))
      MantisToGTask.convertToGenericTask(issue)
    } catch {
      case e: RemoteException =>
        throw MantisUtils.convertException(e)
    }

  }

  override def loadData(): util.List[GTask] = {
    try {
      val mgr = MantisManagerFactory.createMantisManager(setup)
      val queryId = config.getQueryId
      val pkey = if (config.getProjectKey == null) null
      else new BigInteger(config.getProjectKey)
      val issues = if (queryId == null) mgr.getIssuesByProject(pkey)
      else mgr.getIssuesByFilter(pkey, BigInteger.valueOf(queryId))
      convertToGenericTasks(issues)
    } catch {
      case e: RemoteException =>
        throw MantisUtils.convertException(e)
    }
  }

  private def convertToGenericTasks(issues: util.List[IssueData]) = {
    val result = new util.ArrayList[GTask](issues.size)
    import scala.collection.JavaConversions._
    for (issue <- issues) {
      val task = MantisToGTask.convertToGenericTask(issue)
      result.add(task)
    }
    result
  }

  override def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask], monitor: ProgressMonitor,
                        rows: Iterable[FieldRow[_]]): SaveResult = {
    val mgr = MantisManagerFactory.createMantisManager(setup)
    try {
      val mntProject = mgr.getProjectById(new BigInteger(config.getProjectKey))
      val users = if (config.isFindUserByName) mgr.getUsers
      else new util.ArrayList[AccountData]
      val converter = new GTaskToMatis(mntProject, users)
      val saver = new MantisTaskSaver(mgr)
      val rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, rows,
        setup.host)
      rb.getResult
    } catch {
      case e: RemoteException =>
        throw MantisUtils.convertException(e)
    }
  }
}
