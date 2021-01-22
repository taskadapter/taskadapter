package com.taskadapter.webui

import java.io.File

import com.taskadapter.auth.{AuthorizedOperations, AuthorizedOperationsImpl}
import com.taskadapter.webui.service.{EditorManager, Preservices}
import com.taskadapter.webui.uiapi.ConfigsTempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ConfigureSystemPanelTest extends FunSpec with Matchers with ConfigsTempFolder {
  val adminOps = createAdminOps()
  TestLoginUtil.loginAsAdmin()

  // TODO 14 Vaadin 14 requires a completely new testing mechanism
  ignore("disables admin section editing in non-licensed version") {
    withTempFolder { folder =>
      val preservices = createTestPreservices(folder)
      val container = new ConfigureSystemPage()
      val auto = UiTester.findElement(container, Page.message("configurePage.anonymousErrorReporting"))
      val attr = auto.getElement().getAttribute("disabled")
      attr.toBoolean shouldBe true
    }
  }

  // TODO 14 Vaadin 14 requires a completely new testing mechanism
  ignore("enables admin section editing in licensed version") {
    withTempFolder { folder =>
      val preservices = createTestPreservices(folder)
      val container = new ConfigureSystemPage()
      val auto = UiTester.findElement(container, Page.message("configurePage.anonymousErrorReporting"))
      val attr = auto.getElement().getAttribute("disabled")
      attr.toBoolean shouldBe false
    }
  }

  def createTestPreservices(rootFolder: File): Preservices = {
    new Preservices(rootFolder, EditorManager.fromResource("editors.txt"))
  }

  def createAdminOps(): AuthorizedOperations = {
    new AuthorizedOperationsImpl("admin")
  }

}
