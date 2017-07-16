package com.taskadapter.model

import com.taskadapter.connector.definition.TaskId

sealed trait RelationType

case object Precedes extends RelationType

case class GRelation(taskId: TaskId, relatedTaskId: TaskId, `type`: RelationType)
