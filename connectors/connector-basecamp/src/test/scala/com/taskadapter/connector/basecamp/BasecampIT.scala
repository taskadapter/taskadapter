package com.taskadapter.connector.basecamp

import com.taskadapter.connector.basecamp.transport.{BaseCommunicator, ObjectAPIFactory}
import com.taskadapter.connector.testlib.CommonTestChecks
import com.taskadapter.model.GTaskBuilder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class BasecampIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with BasecampTestProject {
  private val factory = new ObjectAPIFactory(new BaseCommunicator)

  it("task is created and loaded") {
    withTempProject { todoListKey =>
      CommonTestChecks.taskIsCreatedAndLoaded(getConnector(todoListKey),
        GTaskBuilder.withRandom(BasecampField.content),
        BasecampFieldBuilder.getDefault(), BasecampField.content,
        CommonTestChecks.skipCleanup)
    }
  }
  it("task is updated") {
    withTempProject { todoListKey =>
      CommonTestChecks.taskCreatedAndUpdatedOK(TestBasecampConfig.setup().host,
        getConnector(todoListKey), BasecampFieldBuilder.getDefault(),
        GTaskBuilder.withRandom(BasecampField.content),
        BasecampField.content.name, "new value",
        CommonTestChecks.skipCleanup)
    }
  }

  def getConnector(todoListKey: String): BasecampConnector = {
    val config = TestBasecampConfig.config
    config.setTodoKey(todoListKey)
    new BasecampConnector(config, TestBasecampConfig.setup(), factory)
  }

}