package com.taskadapter.webui.pages

import com.taskadapter.webui.Page.message
import com.vaadin.server.ExternalResource
import com.vaadin.ui.{Component, Link}

object WebAppUpdater {
  private val TASKADAPTER_DOWNLOAD_URL = "http://www.taskadapter.com/download"

  def addDownloadLink(): Component = {
    val downloadLink = new Link
    downloadLink.setResource(new ExternalResource(TASKADAPTER_DOWNLOAD_URL))
    downloadLink.setCaption(message("supportPage.openDownloadPage"))
    downloadLink.setTargetName("_new")
    downloadLink
  }
}
