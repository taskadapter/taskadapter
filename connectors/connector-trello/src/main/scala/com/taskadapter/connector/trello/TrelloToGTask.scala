package com.taskadapter.connector.trello

import com.julienvey.trello.domain.{Argument, Card}
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._

import scala.collection.JavaConverters._

object TrelloToGTask {
  def convert(listCache: ListCache, card: Card): GTask = {
    val task = new GTask()

    val fakeEmptyId = 0L
    val key = card.getId
    task.setId(fakeEmptyId)
    task.setKey(key)
    // must set source system id, otherwise "update task" is impossible later
    task.setSourceSystemId(new TaskId(fakeEmptyId, key))

    task.setValue(Summary, card.getName)
    task.setValue(DueDate, card.getDue)
    task.setValue(UpdatedOn, card.getDateLastActivity)
    task.setValue(Description, card.getDesc)
    task.setValue(TrelloField.listId, card.getIdList)
    task.setValue(TrelloField.listName, listCache.getListNameById(card.getIdList))
    // note - this sends a request to Trello server! Trello REST API limitations.
    val actions = card.getActions(new Argument("filter", "createCard"))
    if (!actions.isEmpty) {
      val creator = actions.asScala.head.getMemberCreator
      task.setValue(ReporterLoginName, creator.getUsername)
      task.setValue(ReporterFullName, creator.getFullName)
    }
    task
  }
}
