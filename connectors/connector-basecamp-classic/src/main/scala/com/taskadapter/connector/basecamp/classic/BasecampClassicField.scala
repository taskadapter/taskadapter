package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.Field
import com.taskadapter.model._

object BasecampClassicField {
  val content = Field("Content")
  val doneRatio = Field.float("Done Ratio")
  val dueDate = Field.date("Due date")
  val createdOn = Field.date("Created On")
  val closedOn = Field.date("Closed On")
  val updatedOn = Field.date("Updated On")
  val assignee = Field.user("assignee")

  val fields = List(content, doneRatio, dueDate, assignee, createdOn, updatedOn, closedOn)

  def suggestedStandardFields = Map(
    assignee -> Assignee,
    doneRatio -> DoneRatio,
    dueDate -> DueDate,
    createdOn -> CreatedOn,
    updatedOn -> UpdatedOn,
    closedOn -> ClosedOn,
    content -> Summary
  )
}
