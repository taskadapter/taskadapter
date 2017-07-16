package com.taskadapter.connector.definition

import com.taskadapter.model.GTask

class SaveResultBuilder {
  private var targetFileAbsolutePath = ""
  /**
    * Number of updated tasks.
    */
  private var updatedTasksNumber = 0
  /**
    * Number of created tasks.
    */
  private var createdTasksNumber = 0

  // maps original task Key --> new task info when new tasks are created
  private val idToRemoteKeyMap = new scala.collection.mutable.ListBuffer[(Long, TaskId)]
  private val generalErrors = new scala.collection.mutable.ListBuffer[Throwable]
  private val taskErrors = new scala.collection.mutable.ListBuffer[TaskError[Throwable]]

  def setTargetFileAbsolutePath(targetFileAbsolutePath: String): Unit = {
    this.targetFileAbsolutePath = targetFileAbsolutePath
  }

  def addCreatedTask(originalId: Long, newKey: TaskId): Unit = {
    idToRemoteKeyMap += ((originalId, newKey))
    createdTasksNumber += 1
  }

  def addUpdatedTask(originalId: Long, newId: TaskId): Unit = {
    idToRemoteKeyMap += ((originalId, newId))
    updatedTasksNumber += 1
  }

  def getRemoteKey(originalId: Long): TaskId = idToRemoteKeyMap.find(long => long._1 == originalId).map(e => e._2).orNull

  def addGeneralError(e: Throwable): Unit = {
    generalErrors += e
  }

  def addTaskError(task: GTask, e: Throwable): Unit = {
    taskErrors += new TaskError[Throwable](task, e)
  }

  def getResult = new SaveResult(targetFileAbsolutePath, updatedTasksNumber, createdTasksNumber,
    idToRemoteKeyMap.toList,
    generalErrors.toList,
    taskErrors.toList)
}
