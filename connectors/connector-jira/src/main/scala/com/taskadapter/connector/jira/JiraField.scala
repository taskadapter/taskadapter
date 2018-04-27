package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.model._

import scala.collection.JavaConverters._

/**
  * JIRA field names
  */
object JiraField {

  /** JIRA supports several components per task, but we support only one value for now */
  val component = Field("Component")

  val summary = Field("Summary")
  val description = Field("Description")
  val taskType = Field("Task Type")
  val status = Field("Status")
  /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
     enabled for tasks by default. let's unselect this field by default
     to avoid user confusion.
   */
  val estimatedTime = Field.float("Estimated Time") // should not be selected by default
  val assignee = Field.user("Assignee")
  val reporter = Field.user("Reporter")
  val dateCreated = Field.date("Date Created")
  val dueDate = Field.date("Due Date")
  val priority = Field.integer("Priority")
  val id = Field("Id")

  val fields = List(id, component, summary, status, description, taskType, estimatedTime, assignee,
    dateCreated,
    dueDate, priority)

  def fieldsAsJava(): util.List[Field] = fields.asJava

  // id field is not in the suggested list because typically
  // id from one system cannot be directly used as id in another system.
  private def suggestedStandardFields = Map(summary -> Summary,
    component -> Components,
    description -> Description, taskType -> TaskType,
    // estimated time is not a part of standard JIRA 7 anymore
//    estimatedTime -> EstimatedTime,
    status -> TaskStatus,
    assignee -> Assignee,
    // removing "Reporter" for now because export to JIRA gives
    // "status=400, errors={reporter=Field 'reporter' cannot be set. It is not on the appropriate screen, or unknown."
//    reporter -> Reporter,
    dateCreated -> CreatedOn,
    dueDate -> DueDate,
    priority -> Priority)

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
