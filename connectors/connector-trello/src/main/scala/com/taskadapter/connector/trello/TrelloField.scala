package com.taskadapter.connector.trello

import com.taskadapter.model._

object TrelloField {

  val listId = Field("List Id")
  val listName = TaskStatus

  val fields = List(listName, listId, Summary, ReporterFullName, ReporterLoginName, Description, DueDate, UpdatedOn)
  val defaultFieldsForNewConfig = fields
    .filter(_ != listId)
    .filter(_ != ReporterFullName)
    .filter(_ != ReporterLoginName)
    .filter(_ != UpdatedOn)
}
