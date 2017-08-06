package com.taskadapter.webui.config

import com.taskadapter.config.ConfigStorage
import com.taskadapter.web.uiapi.ConfigId
import com.taskadapter.webui.uiapi.TempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ConfigStorageTest extends FunSpec with Matchers with TempFolder {
  private val configName = "some_config_name"
  private val login = "autotest"

  it("config can be created in storage") {
    withTempFolder { folder =>
      val storage = new ConfigStorage(folder)
      val path = storage.createNewConfig(login, configName, "jira", "value1", "jira", "value2", "mappings?")
      val configId = ConfigId(login, path)
      val config = storage.getConfig(configId)
      config.isDefined shouldBe true
      config.get.getId shouldBe path
    }
  }

  it("config is deleted") {
    withTempFolder { folder =>
      val storage = new ConfigStorage(folder)
      storage.createNewConfig(login, configName, "jira", "value1", "jira", "value2", "mappings?")
      val configId = ConfigId(login, configName)
      storage.delete(configId)
      storage.getConfig(configId) shouldBe None
    }
  }
}
