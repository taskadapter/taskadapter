package com.taskadapter.connector.trello

import com.taskadapter.connector.NewConnector
import com.taskadapter.connector.testlib.CommonTestChecks
import com.taskadapter.model.{GTask, GTaskBuilder}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TrelloIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {
  val setup = TrelloTestConfig.getSetup
  val config = TrelloTestConfig.getConfig
  val api = TrelloApiFactory.createApi(setup.password, setup.apiKey)

  def getConnector(): NewConnector = new TrelloConnector(config, setup)

  it("tasks are created without errors") {
    val task = buildTask
    CommonTestChecks.createsTasks(getConnector(), TrelloFieldBuilder.getDefault(),
      List(task),
      CommonTestChecks.skipCleanup)
  }

  it("task is created and updated") {
    CommonTestChecks.taskCreatedAndUpdatedOK("",
      getConnector(), TrelloFieldBuilder.getDefault(),
      buildTask, TrelloField.name.name, CommonTestChecks.skipCleanup)
  }

  def buildTask: GTask = {
    val task = GTaskBuilder.withRandom(TrelloField.name)
    task.setValue(TrelloField.listName, "Stuff to try (this is a list)")
    task
  }
}
