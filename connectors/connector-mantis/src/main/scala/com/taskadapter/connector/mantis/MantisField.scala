package com.taskadapter.connector.mantis

import com.taskadapter.connector.Field
import com.taskadapter.model._

object MantisField {

  val id = Field("Id")
  val summary = Field("Summary")
  val description = Field("Description")
  val assignee = Field("Assignee")
  val dueDate = Field.date("Due Date")
  val createdOn = Field.date("Created On")
  val updatedOn = Field.date("Updated On")
  val priority = Field.integer("Priority")

  val fields = List(summary, description, assignee, dueDate, createdOn, updatedOn, priority)

  private def suggestedStandardFields = Map(id -> Id, summary -> Summary,
    description -> Description,
    assignee -> Assignee,
    dueDate -> DueDate,
    createdOn -> CreatedOn,
    updatedOn -> UpdatedOn,
    priority -> Priority
  )

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}