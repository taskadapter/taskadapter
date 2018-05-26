package com.taskadapter.connector.trello

import com.taskadapter.model._

object TrelloField {

  val listId = Field("List Id")
  val listName = Field("List name")

  val fields = List(listName, listId, Summary, Description, DueDate, UpdatedOn, TaskStatus)
  val defaultFieldsForNewConfig = fields.filter(_ != listId)
}
