package com.taskadapter.connector.basecamp

import com.taskadapter.connector.basecamp.transport.{BaseCommunicator, ObjectAPIFactory}
import com.taskadapter.connector.testlib.CommonTestChecks
import com.taskadapter.model.GTaskBuilder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class BasecampNewIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with BasecampTestProject {
  private val factory = new ObjectAPIFactory(new BaseCommunicator)

  describe("Basecamp") {
    withTempProject { todoListKey =>
      it("task is created and loaded") {
        CommonTestChecks.taskIsCreatedAndLoaded(getConnector(),
          GTaskBuilder.withRandom(BasecampField.description),
          BasecampFieldBuilder.getDefault(), BasecampField.description,
          CommonTestChecks.skipCleanup)
      }


      it("task is updated") {
        CommonTestChecks.taskCreatedAndUpdatedOK(TestBasecampConfig.setup().host,
          getConnector(), BasecampFieldBuilder.getDefault(),
          GTaskBuilder.withRandom(BasecampField.description),
          BasecampField.description.name, "new value",
          CommonTestChecks.skipCleanup)
      }

      def getConnector(): BasecampConnector = {
        val config = TestBasecampConfig.config
        new BasecampConnector(config, TestBasecampConfig.setup(), factory)
      }
    }
  }
}