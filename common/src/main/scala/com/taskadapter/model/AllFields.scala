package com.taskadapter.model

/**
  * temporary adapter to migrate from scala back to simple java (2021)
  */
object AllFields {
  var summary = Summary
  var description = Description
  var taskType = TaskType
  val doneRatio = DoneRatio
  val dueDate = DueDate
  val priority = Priority
  val assigneeLoginName = AssigneeLoginName
  val assigneeFullName = AssigneeFullName
  val createdOn = CreatedOn
  val updatedOn = UpdatedOn
  val closedOn = ClosedOn
  val components = Components
  val estimatedTime = EstimatedTime
  val id = Id
  val key = Key
  val startDate = StartDate
  val targetVersion = TargetVersion
  val taskStatus = TaskStatus
  val reporterLoginName = ReporterLoginName
  val reporterFullName = ReporterFullName
}
