package com.taskadapter.connector.basecamp

import com.taskadapter.connector.basecamp.transport.{BaseCommunicator, ObjectAPIFactory}
import com.taskadapter.connector.testlib.CommonTestChecks
import com.taskadapter.model.GTaskBuilder
import org.junit.runner.RunWith

// Tired of re-creating Basecamp demo accounts. we don't have any Basecamp users, so
// let's just ignore these tests.
@RunWith(classOf[JUnitRunner])
class BasecampIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with BasecampTestProject {
  private val factory = new ObjectAPIFactory(new BaseCommunicator)

  ignore("task is created and loaded") {
    withTempProject { todoListKey =>
      CommonTestChecks.taskIsCreatedAndLoaded(getConnector(todoListKey),
        GTaskBuilder.gtaskWithRandom(BasecampField.content),
        BasecampFieldBuilder.getDefault(), java.util.Arrays.asList(BasecampField.content),
        CommonTestChecks.skipCleanup)
    }
  }
  ignore("task is updated") {
    withTempProject { todoListKey =>
      CommonTestChecks.taskCreatedAndUpdatedOK(TestBasecampConfig.setup().getHost,
        getConnector(todoListKey), BasecampFieldBuilder.getDefault(),
        GTaskBuilder.gtaskWithRandom(BasecampField.content),
        BasecampField.content, "new value",
        CommonTestChecks.skipCleanup)
    }
  }

  def getConnector(todoListKey: String): BasecampConnector = {
    val config = TestBasecampConfig.config
    config.setTodoKey(todoListKey)
    new BasecampConnector(config, TestBasecampConfig.setup(), factory)
  }

}