package com.taskadapter.connector.trello

import com.julienvey.trello.domain.TList

class ListCache(lists: Seq[TList]) {
  def getListIdByName(listName: String): Option[String] = lists.find(_.getName == listName).map(_.getId)

  def getListNameById(listId: String) = lists.find(_.getId == listId).map(_.getName).getOrElse("")
}
