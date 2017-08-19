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
  val assignee = Field.user("Assignee")
  val reporter = Field.user("Reporter")
  val dueDate = Field.date("Due Date")
  val priority = Field.integer("Priority")
  val id = Field("Id")

  val fields = List(id, summary, description, taskType, estimatedTime, assignee, dueDate, priority)

  def fieldsAsJava(): util.List[Field] = fields.asJava

  // id field is not in the suggested list because typically
  // id from one system cannot be directly used as id in another system.
  private def suggestedStandardFields = Map(summary -> Summary,
    description -> Description, taskType -> TaskType,
    // estimated time is not a part of standard JIRA 7 anymore
//    estimatedTime -> EstimatedTime,
    assignee -> Assignee,
    // removing "Reporter" for now because export to JIRA gives
    // "status=400, errors={reporter=Field 'reporter' cannot be set. It is not on the appropriate screen, or unknown."
//    reporter -> Reporter,
    dueDate -> DueDate,
    priority -> Priority)

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
