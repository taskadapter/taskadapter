package com.taskadapter.connector.redmine

import com.taskadapter.connector.Field

import java.util
import scala.collection.JavaConverters._

object RedmineField {
  // Redmine field names as loaded from Redmine
  val summary = Field("Summary")
  val description = Field("Description")
  val taskType = Field("Tracker type")
  val estimatedTime = Field.float("Estimated time")

  /**
    * %% complete (e.g. "30%"). int value
    */
  val doneRatio = Field.float("Done ratio")
  val assignee = Field("Assignee")
  val dueDate = Field.date("Due Date")
  val startDate = Field.date("Start Date")
  val createdOn = Field.date("Created On")
  val updatedOn = Field.date("Updated On")
  val taskStatus = Field("Task status")
  val targetVersion = Field("Target Version")
  val priority = Field.float("Priority")

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

  def fieldsAsJava(): util.List[Field] = fields.asJava
}
