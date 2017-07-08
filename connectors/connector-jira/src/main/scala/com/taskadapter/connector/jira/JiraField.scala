package com.taskadapter.connector.jira

import scala.collection.JavaConverters._

/**
  * JIRA field names
  */
object JiraField {

  def summary = "Summary"
  def description = "Description"
  def taskType = "Task Type"
  /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
     enabled for tasks by default. let's unselect this field by default
     to avoid user confusion.
   */
  def estimatedTime = "Estimated Time" // should not be selected by default
  def assignee = "Assignee"
  def dueDate = "Due Date"
  def priority = "Priority"
  def environment = "Environment" // should not be selected by default

  def fields = List(summary, description, taskType, estimatedTime, assignee, dueDate, priority, environment)

  def fieldsAsJava = fields.asJava
}
