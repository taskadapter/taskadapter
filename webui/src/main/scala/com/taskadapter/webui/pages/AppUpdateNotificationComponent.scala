package com.taskadapter.webui.pages

import com.taskadapter.webui.service.CurrentVersionLoader
import com.taskadapter.webui.{LastVersionLoader, Page, VersionComparator}
import com.vaadin.ui.themes.ValoTheme
import com.vaadin.ui.{Component, HorizontalLayout, Label}

class AppUpdateNotificationComponent {
  val lastAvailableVersion = LastVersionLoader.loadLastVersion
  val currentVersion = new CurrentVersionLoader().getCurrentVersion

  def ui: Component = {
    val layout = new HorizontalLayout
    layout.setSpacing(true)
    layout.setHeight("50px")

    val outdated = VersionComparator.isCurrentVersionOutdated(currentVersion, lastAvailableVersion)
    if (outdated) {
      val message = Page.message("appUpdaterNotification.versionOutdated", currentVersion, lastAvailableVersion)
      val label = new Label(message)
      label.addStyleName("important-notification-label")
      layout.addComponent(label)
      val link = WebAppUpdater.addDownloadLink
      link.addStyleName(ValoTheme.LABEL_BOLD)
      layout.addComponent(link)
    }
    layout.setVisible(outdated)
    layout
  }
}
