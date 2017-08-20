package com.taskadapter.launcher

import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TALauncherTest extends FunSpec with Matchers {

  it("portParameterFound") {
    val args = Array("something", "--port=9090", "something else")
    assertEquals(9090, TALauncher.findPortNumberInArgs(args))
  }

  it("defaultPortReturnedWhenNoParameter") {
    val args = Array("something and something else")
    assertEquals(10842, TALauncher.findPortNumberInArgs(args))
  }

  it("openInBrowserArgumentDetected") {
    val args = Array(TALauncher.PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER)
    assertTrue(TALauncher.needToOpenBrowser(args))
  }
}
