package com.taskadapter.connector.testlib

import java.util

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.{PreviouslyCreatedTasksCache, PreviouslyCreatedTasksResolver}
import com.taskadapter.model.GTask

/**
  * Keeps old task -> new task cache that can be used for "update" operations in tests.
  */
class StatefulTestTaskSaver(connector: NewConnector, targetLocation: String) {

  private val taskIdsSeq = collection.mutable.ArrayBuffer.empty[(TaskId, TaskId)]

  def saveAndLoad(task: GTask, rows: Seq[FieldRow[_]]): GTask = {
    val cache = PreviouslyCreatedTasksCache("1", targetLocation, taskIdsSeq)
    val resolver = new PreviouslyCreatedTasksResolver(cache)
    val result = connector.saveData(resolver, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows)

    val ids = result.keyToRemoteKeyList.head
    val newTaskId = ids._2
    taskIdsSeq += ((newTaskId, newTaskId))

    val remoteKeys = result.getRemoteKeys
    val remoteKey = remoteKeys.iterator.next
    connector.loadTaskByKey(remoteKey, rows)
  }
}
