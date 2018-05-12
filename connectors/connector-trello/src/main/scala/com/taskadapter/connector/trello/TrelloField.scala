package com.taskadapter.connector.trello

import com.taskadapter.connector.Field
import com.taskadapter.model._

object TrelloField {
  val id = Field.integer("Id")
  val name = Field("Name")
  val description = Field("Description")

  val dueDate = Field.date("Due Date")
  val updatedOn = Field.date("Updated On")
  val listId = Field("List Id")
  val listName = Field("List name")

  def fields = List(
    name,
    description,
    dueDate,
    updatedOn,
    listId,
    listName)

  // id field is not in the suggested list because typically
  // id from one system cannot be directly used as id in another system.
  def suggestedStandardFields = Map(
    name -> Summary, description -> Description,
    dueDate -> DueDate,
    updatedOn -> UpdatedOn,
    listName -> TaskStatus)

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
