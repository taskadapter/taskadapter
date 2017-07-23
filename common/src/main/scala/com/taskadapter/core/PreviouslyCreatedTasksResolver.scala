package com.taskadapter.core

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.GTask

class PreviouslyCreatedTasksResolver(list: Seq[(TaskId, TaskId)] = Seq()) {
  val mapLeftToRight = list.map(i => i._1 -> i._2).toMap
  val mapRightToLeft = list.map(i => i._2 -> i._1).toMap

  def findSourceSystemIdentity(task: GTask): Option[TaskId] = {
    if (task.getSourceSystemId != null && mapLeftToRight.contains(task.getSourceSystemId)) {
      Some(mapLeftToRight(task.getSourceSystemId))
    } else {
      None
    }
  }
}
