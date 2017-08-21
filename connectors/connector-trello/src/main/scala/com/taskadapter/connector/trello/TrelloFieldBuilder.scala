package com.taskadapter.connector.trello

import com.taskadapter.connector.FieldRow

object TrelloFieldBuilder {
  def getDefault(): List[FieldRow] = {
    List(
      FieldRow(TrelloField.listName, TrelloField.listName, ""),
      FieldRow(TrelloField.name, TrelloField.name, ""),
    )
  }
}
