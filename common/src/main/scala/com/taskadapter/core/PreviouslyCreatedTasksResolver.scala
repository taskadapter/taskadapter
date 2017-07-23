package com.taskadapter.core

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.GTask

class PreviouslyCreatedTasksResolver(cache: PreviouslyCreatedTasksCache) {
  val mapLeftToRight = cache.items.map(i => i._1 -> i._2).toMap
  val mapRightToLeft = cache.items.map(i => i._2 -> i._1).toMap

  def findSourceSystemIdentity(task: GTask, targetLocation: String): Option[TaskId] = {
    val mapToSearchIn = if (targetLocation == cache.location2) mapLeftToRight
    else mapRightToLeft

    if (task.getSourceSystemId != null && mapToSearchIn.contains(task.getSourceSystemId)) {
      Some(mapToSearchIn(task.getSourceSystemId))
    } else {
      None
    }
  }
}

object PreviouslyCreatedTasksResolver {
  def empty: PreviouslyCreatedTasksResolver =
    new PreviouslyCreatedTasksResolver(PreviouslyCreatedTasksCache("1", "2", Seq()))
}