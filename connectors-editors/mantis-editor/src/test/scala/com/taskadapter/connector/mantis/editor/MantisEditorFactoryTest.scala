package com.taskadapter.connector.mantis.editor

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{ProjectNotSetException, ServerURLNotSetException}
import com.taskadapter.connector.mantis.{MantisConfig, MantisConnector}
import com.taskadapter.connector.testlib.TempFolder
import com.taskadapter.web.service.Sandbox
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import java.util.Collections

@RunWith(classOf[JUnitRunner])
class MantisEditorFactoryTest extends FunSpec with Matchers with TempFolder {
  val factory = new MantisEditorFactory
  val config = new MantisConfig
  val setup = WebConnectorSetup(MantisConnector.ID, Option.empty, "label1", "host", "user", "password", false, "")

  it("miniPanelInstanceIsCreated") {
    withTempFolder { folder =>
      factory.getMiniPanelContents(new Sandbox(false, folder), new MantisConfig, setup)
    }
  }

  describe("config validation for save") {
    it("gives error for empty project key") {
      val errors = factory.validateForSave(new MantisConfig, setup, Collections.emptyList())
      errors.get(0) shouldBe a[ProjectNotSetException]
    }
    it("gives error for empty host name") {
      val errors = factory.validateForSave(new MantisConfig, setup.copy(host = ""), Collections.emptyList())
      errors.get(0) shouldBe a[ServerURLNotSetException]
    }
  }

  describe("config validation for load") {
    it("gives error when both project key and query id are empty") {
      val errors = factory.validateForLoad(config, setup)
      errors.get(0) shouldBe a[BothProjectKeyAndQueryIsAreMissingException]
    }
    it("passes when project key is empty but query id is defined") {
      config.setQueryId(123l)
      val errors = factory.validateForLoad(config, setup)
      errors.size() shouldBe 0
    }
    it("passes when project key is defined but query id is empty") {
      config.setProjectKey("project1")
      val errors = factory.validateForLoad(config, setup)
      errors.size() shouldBe 0
    }
  }
}