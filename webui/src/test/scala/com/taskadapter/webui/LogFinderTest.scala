package com.taskadapter.webui

import com.taskadapter.webui.uiapi.ConfigsTempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class LogFinderTest extends FunSpec with Matchers with ConfigsTempFolder {
  it("finds the log file") {
    val location = LogFinder.getLogFileLocation()
    location should include("taskadapter.log")
  }
}
