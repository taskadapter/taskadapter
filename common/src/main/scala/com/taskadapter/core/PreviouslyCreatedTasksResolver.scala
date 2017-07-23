package com.taskadapter.core

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.GTask

class PreviouslyCreatedTasksResolver(map: Map[String, TaskId] = Map[String, TaskId]()) {
  def findSourceSystemIdentity(task: GTask): Option[TaskId] = {
    if (task.getSourceSystemId != null && map.contains(task.getSourceSystemId)) {
      Some(map(task.getSourceSystemId))
    } else {
      None
    }
  }
}
