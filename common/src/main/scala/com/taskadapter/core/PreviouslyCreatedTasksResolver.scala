package com.taskadapter.core

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.GTask

class PreviouslyCreatedTasksResolver(cache: PreviouslyCreatedTasksCache) {
  val mapLeftToRight = cache.items.map(i => i.originalId -> i.newId).toMap
  val mapRightToLeft = cache.items.map(i => i.newId -> i.originalId).toMap

  /**
    * @param task must contain sourceSystemId value
    * @param targetLocation
    */
  def findSourceSystemIdentity(task: GTask, targetLocation: String): Option[TaskId] = {
    findSourceSystemIdentity(task.getSourceSystemId, targetLocation)
  }

  def findSourceSystemIdentity(sourceSystemId: TaskId, targetLocation: String): Option[TaskId] = {
    val mapToSearchIn = if (targetLocation == cache.location2) mapLeftToRight
    else mapRightToLeft

    if (sourceSystemId != null && mapToSearchIn.contains(sourceSystemId)) {
      Some(mapToSearchIn(sourceSystemId))
    } else {
      None
    }
  }
}

object PreviouslyCreatedTasksResolver {
  def empty: PreviouslyCreatedTasksResolver =
    new PreviouslyCreatedTasksResolver(PreviouslyCreatedTasksCache("", "", Seq()))
}