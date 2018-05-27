package com.taskadapter.connector.redmine

import com.taskadapter.model._

object RedmineField {
  val category = Components

  def fields = List(
    category,
    Summary,
    Description,
    TaskType,
    EstimatedTime,
    DoneRatio,
    AssigneeFullName,
    AssigneeLoginName,
    DueDate,
    ReporterFullName,
    ReporterLoginName,
    StartDate,
    CreatedOn,
    UpdatedOn,
    TaskStatus,
    TargetVersion,
    Priority)
}
