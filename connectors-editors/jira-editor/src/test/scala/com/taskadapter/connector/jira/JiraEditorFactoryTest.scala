package com.taskadapter.connector.jira

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.testlib.TempFolder
import com.taskadapter.editor.testlib.VaadinTestHelper
import com.taskadapter.web.service.Sandbox
import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class JiraEditorFactoryTest extends FunSpec with Matchers with TempFolder {

    VaadinTestHelper.initVaadinSession(getClass)

  it ("miniPanelIsCreated") {
    withTempFolder { folder =>
      val factory = new JiraEditorFactory
      factory.getMiniPanelContents(new Sandbox(false, folder), new JiraConfig,
        WebConnectorSetup(JiraConnector.ID, "label1", "host", "user", "password", false, "api"))
    }
  }

  it("serverURLIsRequiredForSave"){
    try {
      new JiraEditorFactory().validateForSave(new JiraConfig, WebConnectorSetup(JiraConnector.ID, "label1", "", "", "", false, ""))
      Assert.fail()
    } catch {
      case e: JiraConfigException =>
        Assert.assertTrue(e.getErrors.contains(JiraValidationErrorKind.HOST_NOT_SET))
    }
  }

  it("projectKeyIsRequiredForSave") {
    try
      new JiraEditorFactory().validateForSave(new JiraConfig, WebConnectorSetup(JiraConnector.ID, "label1", "http://somehost", "", "", false, ""))
    catch {
      case e: JiraConfigException =>
        Assert.assertTrue(e.getErrors.contains(JiraValidationErrorKind.PROJECT_NOT_SET))
    }
  }

  it("subtasksTypeIsRequiredForSave"){
    try {
      val config = new JiraConfig
      config.setProjectKey("someproject")
      // clear the value
      config.setDefaultIssueTypeForSubtasks("")
      new JiraEditorFactory().validateForSave(config, WebConnectorSetup(JiraConnector.ID, "label1", "http://somehost", "", "", false, ""))
      Assert.fail()
    } catch {
      case e: JiraConfigException =>
        Assert.assertTrue(e.getErrors.contains(JiraValidationErrorKind.DEFAULT_SUBTASK_TYPE_NOT_SET))
    }
  }
}
