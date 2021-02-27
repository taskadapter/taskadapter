package com.taskadapter.connector.trello

import org.scalatest.FunSpec

import scala.util.Random

trait TrelloTestProject extends FunSpec {

  val setup = TrelloTestConfig.getSetup
  val client = new TrelloClient(setup.getPassword, setup.getApiKey)

  def withTempBoard(testCode: String => Any) {
    val board = client.createBoard("board-test-" + Random.nextInt(10000))
    try {
      testCode(board.getId)
    }
    finally client.closeBoard(board.getId)
  }

}
