package com.taskadapter.connector.mantis

import java.rmi.RemoteException

import biz.futureware.mantis.rpc.soap.client.IssueData
import com.taskadapter.connector.common.BasicIssueSaveAPI
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.definition.exceptions.{ConnectorException, EntityProcessingException}


final class MantisTaskSaver(val mgr: MantisManager) extends BasicIssueSaveAPI[IssueData] {
  @throws[ConnectorException]
  override def createTask(nativeTask: IssueData): TaskId = try {
    mgr.createIssue(nativeTask)
  } catch {
    case e: RemoteException =>
      throw MantisUtils.convertException(e)
    case e: RequiredItemException =>
      throw new EntityProcessingException(e)
  }

  @throws[ConnectorException]
  override def updateTask(nativeTask: IssueData): Unit = {
    try
      mgr.updateIssue(nativeTask)
    catch {
      case e: RemoteException =>
        throw MantisUtils.convertException(e)
    }
  }
}
