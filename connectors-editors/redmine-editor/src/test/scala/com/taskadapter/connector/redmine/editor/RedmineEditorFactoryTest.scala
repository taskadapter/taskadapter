package com.taskadapter.connector.redmine.editor

import com.taskadapter.connector.definition.WebConnectorSetup
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

  it("mini panel is created") {
    withTempFolder { folder =>
      val factory = new RedmineEditorFactory
      factory.getMiniPanelContents(new Sandbox(true, folder), new RedmineConfig,
        WebConnectorSetup(RedmineConnector.ID, "label1", "http://somehost", "user", "password", false, ""))
    }
  }

}