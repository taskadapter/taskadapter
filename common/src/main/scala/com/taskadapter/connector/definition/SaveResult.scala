package com.taskadapter.connector.definition

import java.util.Date

case class SaveResult(targetFileAbsolutePath: String, updatedTasksNumber: Int, createdTasksNumber: Int,
                      keyToRemoteKeyList: List[(TaskId, TaskId)],
                      generalErrors: List[Throwable],
                      taskErrors: List[TaskError[Throwable]],
                      dateStarted: Date,
                      timeTookSeconds: Int) {

  def getRemoteKeys: List[TaskId] = keyToRemoteKeyList.map(_._2)

  def hasErrors: Boolean = generalErrors.nonEmpty || taskErrors.nonEmpty
}
