package com.taskadapter.webui.config

import java.io.File

object ApplicationSettings {
  /**
    * Calculates default taskadapter root folder. Current implementation
    * returns <code>user.home/.taskadapter</code>.
    *
    * @return task adapter config folder.
    */
  def getDefaultRootFolder(): File = {
    val userHome = System.getProperty("user.home")
    new File(userHome, ".taskadapter")
  }
}
