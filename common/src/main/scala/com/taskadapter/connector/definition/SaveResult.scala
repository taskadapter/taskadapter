package com.taskadapter.connector.definition

class SaveResult(targetFileAbsolutePath: String, updatedTasksNumber: Int, createdTasksNumber: Int,
                 keyToRemoteKeyList: List[(TaskId, TaskId)],
                 generalErrors: List[Throwable],
                 taskErrors: List[TaskError[Throwable]]) {

  def getTargetFileAbsolutePath: String = targetFileAbsolutePath

  def getUpdatedTasksNumber: Int = updatedTasksNumber

  def getCreatedTasksNumber: Int = createdTasksNumber

  def getIdToRemoteKeyList: List[(TaskId, TaskId)] = keyToRemoteKeyList

  def getRemoteKeys: List[TaskId] = keyToRemoteKeyList.map(_._2)

  def getGeneralErrors: List[Throwable] = generalErrors

  def getTaskErrors: List[TaskError[Throwable]] = taskErrors

  def hasErrors: Boolean = !generalErrors.isEmpty || !taskErrors.isEmpty
}
