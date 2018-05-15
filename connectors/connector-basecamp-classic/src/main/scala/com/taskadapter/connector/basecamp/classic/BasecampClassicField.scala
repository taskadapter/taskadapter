package com.taskadapter.connector.basecamp.classic

import com.taskadapter.model._

object BasecampClassicField {
  val content = Field("Content")
  val fields = List(content, DoneRatio, DueDate, Assignee, CreatedOn, UpdatedOn, ClosedOn)
}
