package com.taskadapter.connector.trello

import com.taskadapter.connector.NewConnector
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.testlib.{CommonTestChecks, ITFixture}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.{AllFields, GTask, GTaskBuilder}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class TrelloIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with TrelloTestProject {

  def getConnector(boardId: String): NewConnector = {
    val config = TrelloTestConfig.getConfig
    config.setBoardId(boardId)
    new TrelloConnector(config, setup)
  }

  it("task created and loaded") {
    withTempBoard { boardId =>
      // reporter info is not used when creating a task, but will be used to check value in the created tasks
      val task = buildTask.setValue(AllFields.reporterLoginName, "altest6")
        .setValue(AllFields.reporterFullName, "Alex Skor")
        .setValue(AllFields.description, "desc")
      val fixture = new ITFixture("Trello server", getConnector(boardId), CommonTestChecks.skipCleanup)
      fixture.taskIsCreatedAndLoaded(task,
        java.util.Arrays.asList(AllFields.description, AllFields.summary, TrelloField.listName,
          AllFields.reporterLoginName,
          AllFields.reporterFullName))
    }
  }

  it("task is created and updated") {
    withTempBoard { boardId =>
      CommonTestChecks.taskCreatedAndUpdatedOK("",
        getConnector(boardId), TrelloFieldBuilder.getDefault(),
        buildTask, AllFields.summary, "new value",
        CommonTestChecks.skipCleanup)
    }
  }

  it("proper exception with unknown list name") {
    withTempBoard { boardId =>
      val task = buildTask
      task.setValue(TrelloField.listName, "unknown list")
      val result = getConnector(boardId).saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, TrelloFieldBuilder.getDefault())
      result.getTaskErrors.size shouldBe 1
      val error = result.getTaskErrors.get(0).getError
      error shouldBe a[ConnectorException]
      error.getMessage should include ("Trello list with name 'unknown list' is not found on the requested Trello Board")
    }
  }

  def buildTask: GTask = {
    val task = GTaskBuilder.gtaskWithRandom(AllFields.summary)
    task.setValue(TrelloField.listName, "To Do")
    task
  }
}
