package com.taskadapter.connector.redmine

object RedmineField {
  // Redmine field names as loaded from Redmine
  val summary = "Summary"
  val description = "Description"
  val taskType = "Tracker type"
  val estimatedTime = "Estimated time"
  val doneRatio = "Done ratio"
  val assignee = "Assignee"
  val dueDate = "Due Date"
  val startDate = "Start Date"
  val createdOn = "Created On"
  val updatedOn = "Updated On"
  val taskStatus = "Task status"
  val targetVersion = "Target Version"
  val priority = "Priority"

  def fields = List(summary,
    description,
    taskType,
    estimatedTime,
    doneRatio,
    assignee,
    dueDate,
    startDate,
    createdOn,
    updatedOn,
    taskStatus,
    targetVersion,
    priority)

}
