package com.taskadapter.connector.trello

import com.taskadapter.connector.NewConnector
import com.taskadapter.connector.testlib.CommonTestChecks
import com.taskadapter.model.{GTask, GTaskBuilder}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TrelloIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with TrelloTestProject {

  def getConnector(boardId: String): NewConnector = {
    val config = TrelloTestConfig.getConfig
    config.boardId = boardId
    new TrelloConnector(config, setup)
  }

  it("tasks are created without errors") {
    withTempBoard { boardId =>
      val task = buildTask
      CommonTestChecks.createsTasks(getConnector(boardId), TrelloFieldBuilder.getDefault(),
        List(task),
        CommonTestChecks.skipCleanup)
    }
  }

  it("task is created and updated") {
    withTempBoard { boardId =>
      CommonTestChecks.taskCreatedAndUpdatedOK("",
        getConnector(boardId), TrelloFieldBuilder.getDefault(),
        buildTask, TrelloField.name.name, CommonTestChecks.skipCleanup)
    }
  }

  def buildTask: GTask = {
    val task = GTaskBuilder.withRandom(TrelloField.name)
    task.setValue(TrelloField.listName, "To Do")
    task
  }
}
