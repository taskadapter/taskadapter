package com.taskadapter.connector.definition

case class SaveResult(targetFileAbsolutePath: String, updatedTasksNumber: Int, createdTasksNumber: Int,
                      keyToRemoteKeyList: Seq[TaskKeyMapping],
                      generalErrors: Seq[Throwable],
                      taskErrors: Seq[TaskError]) {

  def getRemoteKeys: Seq[TaskId] = keyToRemoteKeyList.map(_.newId)

  def hasErrors: Boolean = generalErrors.nonEmpty || taskErrors.nonEmpty
}
