package com.taskadapter.connector.trello

import com.julienvey.trello.domain.Board
import com.taskadapter.connector.testlib.HttpCaller

class TrelloClient(key: String, token: String) {
  val credentials = s"key=$key&token=$token"
  val baseUrl = s"https://api.trello.com/1"

  def createBoard(boardName: String): Board = {
    val createBoardUrl = baseUrl + s"/boards?name=${boardName}&${credentials}"
    val board = HttpCaller.post(createBoardUrl, classOf[Board])
    board
  }

  def closeBoard(boardId: String): Board = {
    val url = baseUrl + s"/boards/$boardId?closed=true&${credentials}"
    val board = HttpCaller.put(url, classOf[Board])
    board
  }

}
