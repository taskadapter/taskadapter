package com.taskadapter.connector.basecamp

import com.taskadapter.model._

object BasecampField {
  val content = Field("Content")
  val fields = List(content, DoneRatio, DueDate, Assignee, CreatedOn, UpdatedOn)
}
