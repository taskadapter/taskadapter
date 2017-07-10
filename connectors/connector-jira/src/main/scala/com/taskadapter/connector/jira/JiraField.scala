package com.taskadapter.connector.jira

import com.taskadapter.connector.Field
import java.util
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
  val priority = Field("Priority")
  val environment = Field("Environment") // should not be selected by default

  val fields = List(summary, description, taskType, estimatedTime, assignee, dueDate, priority, environment)

  def fieldsAsJava(): util.List[Field] = fields.asJava
}
