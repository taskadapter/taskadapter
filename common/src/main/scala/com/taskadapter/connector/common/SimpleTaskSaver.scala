package com.taskadapter.connector.common

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.{ProgressMonitor, SaveResultBuilder, TaskId}
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.GTask

class SimpleTaskSaver[N](previouslyCreatedTasks: Map[String, Long],
                         converter: ConnectorConverter[GTask, N],
                         saveAPI: BasicIssueSaveAPI[N],
                         result: SaveResultBuilder, progressMonitor: ProgressMonitor) {

  def saveTasks(parentIssueKey: Option[TaskId], tasks: util.List[GTask], fieldRows: java.lang.Iterable[FieldRow]): Unit = {
    tasks.forEach { task =>
      try {
        if (parentIssueKey.isDefined) task.setParentIdentity(parentIssueKey.get)
        val transformedTask = DefaultValueSetter.adapt(fieldRows, task)
        val withPossiblyNewId = replaceIdIfPreviouslyCreatedByUs(previouslyCreatedTasks, transformedTask)

        val readyForNative = withPossiblyNewId
        val nativeIssueToCreateOrUpdate = converter.convert(readyForNative)
        val identity = TaskId(readyForNative.getId, readyForNative.getKey)
        var newTaskIdentity = identity.id match {
          case 0 =>
            val newTaskKey = saveAPI.createTask(nativeIssueToCreateOrUpdate)
            result.addCreatedTask(task.getKey, newTaskKey) // save originally requested task Id to enable tests and ...?
            newTaskKey
          case some =>
            saveAPI.updateTask(nativeIssueToCreateOrUpdate)
            result.addUpdatedTask(task.getKey, identity)
            identity
        }
        progressMonitor.worked(1)
        if (task.hasChildren) saveTasks(Some(newTaskIdentity), task.getChildren, fieldRows)
      } catch {
        case e: ConnectorException =>
          result.addTaskError(task, e)
        case t: Throwable =>
          result.addTaskError(task, t)
          t.printStackTrace()
      }
    }
  }

  private def replaceIdIfPreviouslyCreatedByUs(previouslyCreatedTasks: Map[String, Long], gTask: GTask): GTask = {
      val result = new GTask(gTask)
      if (gTask.getSourceSystemId != null && previouslyCreatedTasks.contains(gTask.getSourceSystemId)) {
        result.setId(previouslyCreatedTasks(gTask.getSourceSystemId))
      }
      result
  }
}
