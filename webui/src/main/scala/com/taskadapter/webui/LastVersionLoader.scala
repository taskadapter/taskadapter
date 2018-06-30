package com.taskadapter.webui

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.http.HttpCaller
import org.slf4j.LoggerFactory

object LastVersionLoader {
  private val log = LoggerFactory.getLogger("default")

  /**
    * check the last TA version available for download on the website.
    */
  def loadLastVersion: String = {
    val properties = PropertiesUtf8Loader.load("taskadapter.properties")
    val url = properties.getProperty("update_site_url")
    try {
      val lastVersionString = HttpCaller.get(url, classOf[String])
      lastVersionString.trim
    } catch {
      case e: Exception => log.error("Cannot load last version info: " + e.toString)
        "unknown"
    }
  }
}