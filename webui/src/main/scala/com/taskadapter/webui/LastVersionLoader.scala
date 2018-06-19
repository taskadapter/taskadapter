package com.taskadapter.webui

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.http.HttpCaller

object LastVersionLoader {
  /**
    * check the last TA version available for download on the website.
    */
    def loadLastVersion: String = {
      val properties = PropertiesUtf8Loader.load("taskadapter.properties")
      val url = properties.getProperty("update_site_url")
      val lastVersionString = HttpCaller.get(url, classOf[String])
      lastVersionString.trim
    }
}