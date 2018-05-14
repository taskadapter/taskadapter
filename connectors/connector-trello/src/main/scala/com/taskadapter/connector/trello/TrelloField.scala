package com.taskadapter.connector.trello

import com.taskadapter.model._

object TrelloField {

  val listId = Field("List Id")
  val listName = Field("List name")

  // id field is not in the suggested list because typically
  // id from one system cannot be directly used as id in another system.
  val fields = List(listName, listId, Summary, Description, DueDate, UpdatedOn, TaskStatus)
}
