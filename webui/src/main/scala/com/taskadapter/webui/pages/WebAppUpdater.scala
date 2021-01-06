package com.taskadapter.webui.pages

import com.taskadapter.vaadin14shim.Button
import com.taskadapter.webui.Page.message
import com.vaadin.server.Page
import com.vaadin.ui.Component

object WebAppUpdater {
  private val TASKADAPTER_DOWNLOAD_URL = "http://www.taskadapter.com/download"

  def createDownloadLink(): Component = {
    new Button(message("supportPage.openDownloadPage"), _ =>
      Page.getCurrent().getJavaScript().execute("window.open('" + TASKADAPTER_DOWNLOAD_URL + "');")
    )
  }
}
