package com.taskadapter.webui.config

import com.taskadapter.config.ConfigStorage
import com.taskadapter.web.uiapi.ConfigId
import com.taskadapter.webui.uiapi.TempFolder
import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ConfigStorageTest extends FunSpec with Matchers with TempFolder {
  private val ENCRYPTED = "test_encrypted"
  private val login = "autotest"

  it("config can be created in storage") {
    withTempFolder { folder =>
      val storage = new ConfigStorage(folder)
      storage.createNewConfig(login, ENCRYPTED, "jira", "value1", "jira", "value2", "mappings?")
      val configId = ConfigId(login, ENCRYPTED)
      val testConfigFile = storage.getConfig(configId)
      assertNotNull("Test config file not found (might not be saved)", testConfigFile)
    }
  }
}
