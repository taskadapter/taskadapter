package com.taskadapter.webui.uiapi

import java.io.File

import com.taskadapter.PluginManager
import com.taskadapter.config.ConfigStorage
import com.taskadapter.web.uiapi.{UIConfigService, UIConfigStore}
import com.taskadapter.webui.service.EditorManager

object TestUIConfigStoreFactory {
  def createStore(temporaryFolder: File): UIConfigStore = {
    val configStorage = new ConfigStorage(temporaryFolder)
    val editorManager = EditorManager.fromResource("editors.txt")
    val uiConfigService = new UIConfigService(new PluginManager, editorManager)
    new UIConfigStore(uiConfigService, configStorage)
  }
}
