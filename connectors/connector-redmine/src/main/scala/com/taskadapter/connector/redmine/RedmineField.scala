package com.taskadapter.connector.redmine

import com.taskadapter.model._

object RedmineField {
  val category = Components
  val author = Reporter

  def fields = List(author,
    category,
    Summary,
    Description,
    TaskType,
    EstimatedTime,
    DoneRatio,
    AssigneeFullName,
    AssigneeLoginName,
    DueDate,
    StartDate,
    CreatedOn,
    UpdatedOn,
    TaskStatus,
    TargetVersion,
    Priority)
}
