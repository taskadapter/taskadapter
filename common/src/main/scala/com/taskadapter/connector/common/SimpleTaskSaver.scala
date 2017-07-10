package com.taskadapter.connector.common

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.{ProgressMonitor, TaskSaveResultBuilder}
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.GTask

object SimpleTaskSaver {
  private def setKeyToRemoteIdIfPresent(gTask: GTask) = {
    val result = new GTask(gTask)
    val remoteId = gTask.getRemoteId
    if (remoteId != null) result.setKey(remoteId)
    result
  }
}

class SimpleTaskSaver[N](converter: ConnectorConverter[GTask, N],
                               saveAPI: BasicIssueSaveAPI[N],
                               result: TaskSaveResultBuilder, progressMonitor: ProgressMonitor) {

  def saveTasks(parentIssueKey: String, tasks: util.List[GTask], fieldRows: java.lang.Iterable[FieldRow]): Unit = {
    tasks.forEach { task =>
      var newTaskKey = ""
      try {
        if (parentIssueKey != null) task.setParentKey(parentIssueKey)
        // TODO REVIEW Name mismatch. Why default value setter is used to clone tasks? Consider a better name.
        // Something like "TaskMapper", which could be an interface with the only method and several implementations.
        val transformedTask = DefaultValueSetter.adapt(fieldRows, task)
        //                GTask finalGTaskForConversion = setKeyToRemoteIdIfPresent(transformedTask);
        val finalGTaskForConversion = transformedTask
        val nativeIssueToCreateOrUpdate = converter.convert(finalGTaskForConversion)
        newTaskKey = submitTask(finalGTaskForConversion, nativeIssueToCreateOrUpdate)
      } catch {
        case e: ConnectorException =>
          result.addTaskError(task, e)
        case t: Throwable =>
          result.addTaskError(task, t)
          t.printStackTrace()
      }
      progressMonitor.worked(1)
      if (task.hasChildren) saveTasks(newTaskKey, task.getChildren, fieldRows)
    }
  }

  /**
    * @return the newly created task's KEY
    */
  // TODO refactor? we only pass the GTask to check its IDs.
  private def submitTask(task: GTask, nativeTask: N) = {
    var newTaskKey = ""
    if (task.getRemoteId == null || task.getRemoteId.isEmpty) {
      newTaskKey = saveAPI.createTask(nativeTask)
      result.addCreatedTask(task.getId, newTaskKey)
    }
    else {
      newTaskKey = task.getRemoteId
      saveAPI.updateTask(nativeTask)
      result.addUpdatedTask(task.getId, newTaskKey)
    }
    newTaskKey
  }
}
