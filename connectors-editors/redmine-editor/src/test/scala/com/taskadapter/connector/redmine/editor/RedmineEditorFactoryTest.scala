package com.taskadapter.connector.redmine.editor

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException
import com.taskadapter.connector.redmine.{RedmineConfig, RedmineConnector}
import com.taskadapter.connector.testlib.TempFolder
import com.taskadapter.editor.testlib.VaadinTestHelper
import com.taskadapter.web.service.Sandbox
import com.vaadin.flow.data.binder.Binder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}


@RunWith(classOf[JUnitRunner])
class RedmineEditorFactoryTest extends FunSpec with Matchers with TempFolder {

  VaadinTestHelper.initVaadinSession(getClass)
  val factory = new RedmineEditorFactory
  val config = new RedmineConfig()
  val setup = WebConnectorSetup(RedmineConnector.ID, "label1", "http://somehost", "", "", false, "")
  val binder = new Binder[RedmineConfig]

  it("mini panel is created") {
    withTempFolder { folder =>
      factory.getMiniPanelContents(new Sandbox(true, folder), new RedmineConfig, setup)
    }
  }
  describe("config validation for save") {
    it("gives error for empty project key") {
      val exceptions = factory.validateForSave(new RedmineConfig, setup, Seq())
      exceptions.head shouldBe a[ProjectNotSetException]
    }
    it("passes with some project key") {
      config.setProjectKey("project123")
      val errors = factory.validateForSave(config, setup, Seq())
      errors shouldBe empty
    }
  }
}