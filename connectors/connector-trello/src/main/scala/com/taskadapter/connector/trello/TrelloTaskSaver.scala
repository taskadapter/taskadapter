package com.taskadapter.connector.trello

import com.julienvey.trello.Trello
import com.julienvey.trello.domain.Card
import com.taskadapter.connector.common.BasicIssueSaveAPI
import com.taskadapter.connector.definition.TaskId

class TrelloTaskSaver(api: Trello) extends BasicIssueSaveAPI[Card] {

  override def createTask(nativeTask: Card): TaskId = {
    val newCard = api.createCard(nativeTask.getIdList, nativeTask)
    val longId: Int = 0
    TaskId(longId, newCard.getId + "")
  }

  override def updateTask(nativeTask: Card): Unit = api.updateCard(nativeTask)
}
