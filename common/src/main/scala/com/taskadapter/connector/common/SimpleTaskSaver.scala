package com.taskadapter.connector.common

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.{ProgressMonitor, TaskId, SaveResultBuilder}
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.core.TaskKeeper
import com.taskadapter.model.GTask

class SimpleTaskSaver[N](taskKeeper: TaskKeeper, converter: ConnectorConverter[GTask, N],
                         saveAPI: BasicIssueSaveAPI[N],
                         result: SaveResultBuilder, progressMonitor: ProgressMonitor) {

  def saveTasks(parentIssueKey: Option[TaskId], tasks: util.List[GTask], fieldRows: java.lang.Iterable[FieldRow]): Unit = {
    val previouslyCreatedTasks = taskKeeper.loadTasks()
    tasks.forEach { task =>
      try {
        if (parentIssueKey.isDefined) task.setParentIdentity(parentIssueKey.get)
        //        val possiblyNewKey = previouslyCreatedTasks.getOrElse(task.getKey, "")
        val transformedTask = DefaultValueSetter.adapt(fieldRows, task)
        val withPossiblyNewId = replaceIdIfPreviouslyCreatedByUs(previouslyCreatedTasks, transformedTask, task.getKey)

        val readyForNative = withPossiblyNewId
        val nativeIssueToCreateOrUpdate = converter.convert(readyForNative)
        val identity = TaskId(readyForNative.getId, readyForNative.getKey)
        var newTaskKey = submitTask(identity, nativeIssueToCreateOrUpdate)
        taskKeeper.keepTask(task.getKey, newTaskKey.id)

        progressMonitor.worked(1)
        if (task.hasChildren) saveTasks(Some(newTaskKey), task.getChildren, fieldRows)
      } catch {
        case e: ConnectorException =>
          result.addTaskError(task, e)
        case t: Throwable =>
          result.addTaskError(task, t)
          t.printStackTrace()
      }
    }
    taskKeeper.store()
  }

  private def replaceIdIfPreviouslyCreatedByUs(previouslyCreatedTasks: Map[String, Long], gTask: GTask, originalKey:String): GTask = {
      val result = new GTask(gTask)
      if (originalKey!= null && previouslyCreatedTasks.contains(originalKey)) {
        result.setId(previouslyCreatedTasks(originalKey))
      }
      result
  }

  /**
    * @return the newly created task's KEY
    */
  private def submitTask(id: TaskId, nativeTask: N): TaskId = {
    if (id.id == 0) {
      val newTaskKey = saveAPI.createTask(nativeTask)
      result.addCreatedTask(id.id, newTaskKey)
      newTaskKey
    } else {
      saveAPI.updateTask(nativeTask)
      result.addUpdatedTask(id.id, id)
      id
    }
  }
}
