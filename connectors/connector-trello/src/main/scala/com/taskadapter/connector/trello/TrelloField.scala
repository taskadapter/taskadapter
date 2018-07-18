package com.taskadapter.connector.trello

import com.taskadapter.model._

object TrelloField {

  val listId = Field("List Id")
  val listName = TaskStatus

  val fields = List(listName, listId,
    Id,
    Key,
    Summary, ReporterFullName, ReporterLoginName, Description, DueDate, UpdatedOn
  )

  val excludeFromNewConfig = Seq(Id, Key, listId, ReporterFullName, ReporterLoginName, UpdatedOn)

  val defaultFieldsForNewConfig = fields.diff(excludeFromNewConfig)
}
