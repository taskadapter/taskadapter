package com.taskadapter.connector.redmine.editor

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException
import com.taskadapter.connector.redmine.{RedmineConfig, RedmineConnector}
import com.taskadapter.connector.testlib.TempFolder
import com.taskadapter.editor.testlib.VaadinTestHelper
import com.taskadapter.web.service.Sandbox
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}


@RunWith(classOf[JUnitRunner])
class RedmineEditorFactoryTest extends FunSpec with Matchers with TempFolder {

  VaadinTestHelper.initVaadinSession(getClass)
  val factory = new RedmineEditorFactory

  it("mini panel is created") {
    withTempFolder { folder =>
      factory.getMiniPanelContents(new Sandbox(true, folder), new RedmineConfig,
        WebConnectorSetup(RedmineConnector.ID, "label1", "http://somehost", "user", "password", false, ""))
    }
  }
  describe("config validation") {
    it("projectKeyIsRequiredForSave") {
      val exceptions = factory.validateForSave(new RedmineConfig, WebConnectorSetup(RedmineConnector.ID, "label1", "http://somehost", "", "", false, ""), Seq())
      exceptions.head shouldBe a[ProjectNotSetException]
    }
  }
}