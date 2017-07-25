package com.taskadapter.webui.uiapi

import java.io.File
import java.nio.file.{Files, Paths}

object ConfigFolderTestConfigurer {

  /**
    * Configures test config folder with JIRA/Redmine credentials.
    *
    * @return folder with user configs
    */
  def configure(rootFolder: File): Unit = {
    List("JIRA1.json", "Redmine1.json").foreach { resourceName =>
      val adminFolder = new File(rootFolder, "admin")
      adminFolder.mkdirs()
      Files.copy(Paths.get(getClass.getClassLoader.getResource(resourceName).getPath),
        new File(adminFolder, resourceName).toPath)
    }
  }
}