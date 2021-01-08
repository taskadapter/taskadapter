package com.taskadapter.webui.pages

import com.taskadapter.webui.Page.message
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.{ClickEvent, Component, ComponentEventListener, UI}

object WebAppUpdater {
  private val TASKADAPTER_DOWNLOAD_URL = "http://www.taskadapter.com/download"

  def createDownloadLink(): Component = {
    new Button(message("supportPage.openDownloadPage"),
      new ComponentEventListener[ClickEvent[Button]]() {
        override def onComponentEvent(event: ClickEvent[Button]): Unit =
          UI.getCurrent().getPage().executeJs("window.open('" + TASKADAPTER_DOWNLOAD_URL + "');")
      }
    )
  }
}
