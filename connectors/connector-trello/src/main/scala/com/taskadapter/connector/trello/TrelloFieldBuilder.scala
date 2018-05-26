package com.taskadapter.connector.trello

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.Summary

object TrelloFieldBuilder {
  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(TrelloField.listName, TrelloField.listName, ""),
      FieldRow(Summary, Summary, "")
    )
  }
}
