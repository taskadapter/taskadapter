package com.taskadapter.webui.uiapi

import com.taskadapter.web.uiapi.ConfigFolderTestConfigurer

import java.io.File
import org.junit.rules.TemporaryFolder
import org.scalatest.FunSpec

trait ConfigsTempFolder extends FunSpec {
  def withTempFolder(testCode: File => Any) {
    var tempFolder = new TemporaryFolder()
    try {
      tempFolder.create()
      ConfigFolderTestConfigurer.configure(tempFolder.getRoot)
      testCode(tempFolder.getRoot) // "loan" the fixture to the test
    }
    finally tempFolder.delete()
  }

}
