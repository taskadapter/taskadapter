package com.taskadapter.connector.common

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.{ProgressMonitor, SaveResultBuilder, TaskId}
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask
import org.slf4j.LoggerFactory

class SimpleTaskSaver[N](previouslyCreatedTasksResolver: PreviouslyCreatedTasksResolver,
                         converter: ConnectorConverter[GTask, N],
                         saveAPI: BasicIssueSaveAPI[N],
                         result: SaveResultBuilder, progressMonitor: ProgressMonitor,
                         targetLocation: String) {
  private val log = LoggerFactory.getLogger(classOf[SimpleTaskSaver[N]])

  def saveTasks(parentIssueKey: Option[TaskId], tasks: util.List[GTask], fieldRows: Iterable[FieldRow]): Unit = {
    tasks.forEach { task =>
      try {
        if (parentIssueKey.isDefined) task.setParentIdentity(parentIssueKey.get)
        val transformedTask = DefaultValueSetter.adapt(fieldRows, task)
        val withPossiblyNewId = replaceIdentityIfPreviouslyCreatedByUs(previouslyCreatedTasksResolver, transformedTask,
          targetLocation)

        val readyForNative = withPossiblyNewId
        val nativeIssueToCreateOrUpdate = converter.convert(readyForNative)
        val identity = TaskId(readyForNative.getId, readyForNative.getKey)
        var newTaskIdentity = identity.key match {
          case "" | null =>
            val newTaskKey = saveAPI.createTask(nativeIssueToCreateOrUpdate)
            result.addCreatedTask(TaskId(task.getId, task.getKey), newTaskKey) // save originally requested task Id to enable tests and ...?
            newTaskKey
          case some =>
            saveAPI.updateTask(nativeIssueToCreateOrUpdate)
            result.addUpdatedTask(TaskId(task.getId, task.getKey), identity)
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

  private def replaceIdentityIfPreviouslyCreatedByUs(resolver: PreviouslyCreatedTasksResolver, gTask: GTask,
                                                     targetLocation: String): GTask = {
    val result = new GTask(gTask)
    val maybeId = previouslyCreatedTasksResolver.findSourceSystemIdentity(gTask, targetLocation)
    if (maybeId.isDefined) {
      result.setId(maybeId.get.id)
      result.setKey(maybeId.get.key)
    }
    result
  }
}
