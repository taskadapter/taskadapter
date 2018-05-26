package com.taskadapter.connector.trello

import com.julienvey.trello.impl.TrelloBadRequestException
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.NewConnector
import com.taskadapter.connector.testlib.CommonTestChecks
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.{GTask, GTaskBuilder, Summary}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

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
        buildTask, Summary, "new value",
        CommonTestChecks.skipCleanup)
    }
  }

  it("proper exception with wrong list id") {
    withTempBoard { boardId =>
      val task = buildTask
      task.setValue(TrelloField.listName, "unknown list")
        val result = getConnector(boardId).saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, TrelloFieldBuilder.getDefault())
      result.taskErrors.size shouldBe 1
      val error = result.taskErrors.head.getError
      error shouldBe a [TrelloBadRequestException]
      error.getMessage shouldBe "invalid value for idList"
    }
  }


  def buildTask: GTask = {
    val task = GTaskBuilder.withRandom(Summary)
    task.setValue(TrelloField.listName, "To Do")
    task
  }
}
