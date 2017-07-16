package com.taskadapter.connector.definition

class SaveResult(targetFileAbsolutePath: String, updatedTasksNumber: Int, createdTasksNumber: Int,
                 idToRemoteKeyList: List[(Long, TaskId)],
                 generalErrors: List[Throwable],
                 taskErrors: List[TaskError[Throwable]]) {

  def getTargetFileAbsolutePath: String = targetFileAbsolutePath

  def getUpdatedTasksNumber: Int = updatedTasksNumber

  def getCreatedTasksNumber: Int = createdTasksNumber

  def getIdToRemoteKeyList: List[(Long, TaskId)] = idToRemoteKeyList

  def getRemoteKey(id: Long): TaskId = idToRemoteKeyList.find(_._1 == id).map(_._2).orNull

  def getRemoteKeys: List[TaskId] = idToRemoteKeyList.map(_._2)

  def getGeneralErrors: List[Throwable] = generalErrors

  def getTaskErrors: List[TaskError[Throwable]] = taskErrors

  def hasErrors: Boolean = !generalErrors.isEmpty || !taskErrors.isEmpty

//  override def toString: String = "SaveResult{" + "targetFileAbsolutePath='" + targetFileAbsolutePath + '\'' + ", updatedTasksNumber=" + updatedTasksNumber + ", createdTasksNumber=" + createdTasksNumber + ", idToRemoteKeyMap=" + idToRemoteKeyMap + ", generalErrors=" + generalErrors + ", taskErrors=" + taskErrors + '}'
}
