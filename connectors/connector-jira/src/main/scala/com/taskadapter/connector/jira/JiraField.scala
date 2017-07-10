package com.taskadapter.connector.jira

import com.taskadapter.connector.Field
import java.util
import scala.collection.JavaConverters._

/**
  * JIRA field names
  */
object JiraField {

  def summary = Field("Summary")
  def description = Field("Description")
  def taskType = Field("Task Type")
  /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
     enabled for tasks by default. let's unselect this field by default
     to avoid user confusion.
   */
  def estimatedTime = Field.float("Estimated Time") // should not be selected by default
  def assignee = Field("Assignee")
  def dueDate = Field.date("Due Date")
  def priority = Field("Priority")
  def environment = Field("Environment") // should not be selected by default

  def fields = List(summary, description, taskType, estimatedTime, assignee, dueDate, priority, environment)

  def fieldsAsJava(): util.List[Field] = fields.asJava
}
