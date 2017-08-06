package com.taskadapter.webui.config

import com.taskadapter.config.ConfigStorage
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
      val configId = storage.createNewConfig(login, configName, "jira", "value1", "jira", "value2", "mappings")
      val config = storage.getConfig(configId)
      config.isDefined shouldBe true
      config.get.getMappingsString shouldBe "mappings"
    }
  }

  it("config is deleted") {
    withTempFolder { folder =>
      val storage = new ConfigStorage(folder)
      val configId = storage.createNewConfig(login, configName, "jira", "value1", "jira", "value2", "mappings")
      storage.delete(configId)
      storage.getConfig(configId) shouldBe None
    }
  }
}
