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
  private val idToRemoteKeyMap = new scala.collection.mutable.ListBuffer[(TaskId, TaskId)]
  private val generalErrors = new scala.collection.mutable.ListBuffer[Throwable]
  private val taskErrors = new scala.collection.mutable.ListBuffer[TaskError]

  def setTargetFileAbsolutePath(targetFileAbsolutePath: String): Unit = {
    this.targetFileAbsolutePath = targetFileAbsolutePath
  }

  def addCreatedTask(original: TaskId, newKey: TaskId): Unit = {
    idToRemoteKeyMap += ((original, newKey))
    createdTasksNumber += 1
  }

  def addUpdatedTask(original: TaskId, newId: TaskId): Unit = {
    idToRemoteKeyMap += ((original, newId))
    updatedTasksNumber += 1
  }

  def getRemoteKey(original: TaskId): TaskId = idToRemoteKeyMap.find(long => long._1 == original).map(e => e._2).orNull

  def addGeneralError(e: Throwable): Unit = {
    generalErrors += e
  }

  def addTaskError(task: GTask, e: Exception): Unit = {
    taskErrors += new TaskError(task, e)
  }

  def getResult = SaveResult(targetFileAbsolutePath, updatedTasksNumber, createdTasksNumber,
    idToRemoteKeyMap.toList,
    generalErrors.toList,
    taskErrors.toList
  )
}
