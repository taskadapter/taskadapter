package com.taskadapter.connector.testlib

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskKeyMapping
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.{PreviouslyCreatedTasksCache, PreviouslyCreatedTasksResolver}
import com.taskadapter.model.GTask

import java.util

/**
  * Keeps old task -> new task cache that can be used for "update" operations in tests.
  */
class StatefulTestTaskSaver(connector: NewConnector, targetLocation: String) {

  private val taskIdsSeq = collection.mutable.ArrayBuffer.empty[TaskKeyMapping]

  def saveAndLoad(task: GTask, rows: util.List[FieldRow[_]]): GTask = {
    val cache = PreviouslyCreatedTasksCache("1", targetLocation, taskIdsSeq)
    val resolver = new PreviouslyCreatedTasksResolver(cache)
    val result = connector.saveData(resolver, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows)

    val ids = result.keyToRemoteKeyList.head
    val newTaskId = ids.newId
    taskIdsSeq += new TaskKeyMapping(newTaskId, newTaskId)

    val remoteKeys = result.getRemoteKeys
    val remoteKey = remoteKeys.iterator.next
    connector.loadTaskByKey(remoteKey, rows)
  }
}
