package com.taskadapter.connector.trello

import com.julienvey.trello.domain.{Card, TList}
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._

object TrelloToGTask {
  def convert(listCache: ListCache, card: Card) : GTask = {
    val task = new GTask()

    val fakeEmptyId = 0L
    val key = card.getId
    task.setId(fakeEmptyId)
    task.setKey(key)
    // must set source system id, otherwise "update task" is impossible later
    task.setSourceSystemId(TaskId(fakeEmptyId, key))

    task.setValue(Summary, card.getName)
    task.setValue(DueDate, card.getDue)
    task.setValue(UpdatedOn, card.getDateLastActivity)
    task.setValue(Description, card.getDesc)
    task.setValue(TrelloField.listId, card.getIdList)
    task.setValue(TrelloField.listName, listCache.getListNameById(card.getIdList))

    task
  }
}
