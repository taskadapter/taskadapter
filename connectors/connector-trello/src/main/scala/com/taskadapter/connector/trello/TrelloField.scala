package com.taskadapter.connector.trello

import com.taskadapter.connector.Field
import com.taskadapter.model._

object TrelloField {
  val id = Field("Id")
  val name = Field("Name")
  val description = Field("Description")

  val dueDate = Field.date("Due Date")
  val createdOn = Field.date("Created On")
  val updatedOn = Field.date("Updated On")
  val listId = Field("List Id")
  val listName = Field("List name")

  def fields = List(
    name,
    description,
    dueDate,
    createdOn,
    updatedOn,
    listId,
    listName)

  // id field is not in the suggested list because typically
  // id from one system cannot be directly used as id in another system.
  def suggestedStandardFields = Map(
    name -> Summary, description -> Description,
    dueDate -> DueDate,
    createdOn -> CreatedOn,
    updatedOn -> UpdatedOn,
    listName -> TaskStatus)

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
