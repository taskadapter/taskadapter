package com.taskadapter.connector.jira

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{ProjectNotSetException, ServerURLNotSetException}
import com.taskadapter.connector.testlib.TempFolder
import com.taskadapter.editor.testlib.VaadinTestHelper
import com.taskadapter.web.service.Sandbox
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import java.util.Collections

@RunWith(classOf[JUnitRunner])
class JiraEditorFactoryTest extends FunSpec with Matchers with TempFolder {

  VaadinTestHelper.initVaadinSession(getClass)
  val factory = new JiraEditorFactory

  it("miniPanelIsCreated") {
    withTempFolder { folder =>
      factory.getMiniPanelContents(new Sandbox(false, folder), new JiraConfig,
        WebConnectorSetup(JiraConnector.ID, "label1", "host", "user", "password", false, "api"))
    }
  }

  it("serverURLIsRequiredForSave") {
    val exceptions = factory.validateForSave(new JiraConfig, WebConnectorSetup(JiraConnector.ID, "label1", "", "", "", false, ""), Collections.emptyList())
    exceptions.get(0) shouldBe a[ServerURLNotSetException]
  }

  it("projectKeyIsRequiredForSave") {
    val exceptions = factory.validateForSave(new JiraConfig, WebConnectorSetup(JiraConnector.ID, "label1", "http://somehost", "", "", false, ""), Collections.emptyList())
    exceptions.get(0) shouldBe a[ProjectNotSetException]
  }

  it("subtasksTypeIsRequiredForSave") {
    val config = new JiraConfig
    config.setProjectKey("someproject")
    // clear the value
    config.setDefaultIssueTypeForSubtasks("")
    val exceptions = factory.validateForSave(config, WebConnectorSetup(JiraConnector.ID, "label1", "http://somehost", "", "", false, ""), Collections.emptyList())
    exceptions.get(0) shouldBe a[DefaultSubTaskTypeNotSetException]
  }
}
