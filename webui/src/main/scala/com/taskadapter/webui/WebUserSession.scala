package com.taskadapter.webui

class WebUserSession(val pageContainer: PageContainer) {

  var currentLoginName: Option[String] = None

  def getCurrentUserName: Option[String] = {
    currentLoginName
  }

  def setCurrentUserName(loginName: String): Unit = {
    currentLoginName = Some(loginName)
  }

  def clear(): Unit = {
    currentLoginName = None
  }
}
