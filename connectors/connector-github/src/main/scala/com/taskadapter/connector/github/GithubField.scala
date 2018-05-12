package com.taskadapter.connector.github

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.model._

import scala.collection.JavaConverters._

object GithubField {
  val summary = Field("Summary")
  val description = Field("Description")
  val assignee = Field.user("Assignee")
  val startDate = Field.date("Start Date")
  val createdOn = Field.date("Created On")
  val updatedOn = Field.date("Updated On")

  val fields = List(summary, description, assignee, startDate, createdOn, updatedOn)

  def fieldsAsJava(): util.List[Field] = fields.asJava

  private def suggestedStandardFields = Map(summary -> Summary,
    description -> Description,
    assignee -> Assignee,
    startDate -> StartDate,
    createdOn -> CreatedOn,
    updatedOn -> UpdatedOn
  )

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
