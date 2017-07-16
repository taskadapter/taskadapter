package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.model._

import scala.collection.JavaConverters._

/**
  * JIRA field names
  */
object JiraField {

  val summary = Field("Summary")
  val description = Field("Description")
  val taskType = Field("Task Type")
  /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
     enabled for tasks by default. let's unselect this field by default
     to avoid user confusion.
   */
  val estimatedTime = Field.float("Estimated Time") // should not be selected by default
  val assignee = Field("Assignee")
  val dueDate = Field.date("Due Date")
  val priority = Field.integer("Priority")
  val environment = Field("Environment") // should not be selected by default
  val id = Field("Id")

  val fields = List(id, summary, description, taskType, estimatedTime, assignee, dueDate, priority, environment)

  def fieldsAsJava(): util.List[Field] = fields.asJava

  private def suggestedStandardFields = Map(id -> Id, summary -> Summary,
    description -> Description, taskType -> TaskType,
    // estimated time is not a part of standard JIRA 7 anymore
//    estimatedTime -> EstimatedTime,
    assignee -> Assignee,
    dueDate -> DueDate,
    priority -> Priority)

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
