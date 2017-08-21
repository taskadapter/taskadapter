package com.taskadapter.connector.trello

import com.julienvey.trello.domain.TList

class ListCache(lists: Seq[TList]) {
  def getListIdByName(listName: String) = lists.find(_.getName == listName).map(_.getId).getOrElse("")

  def getListNameById(listId: String) = lists.find(_.getId == listId).map(_.getName).getOrElse("")
}
