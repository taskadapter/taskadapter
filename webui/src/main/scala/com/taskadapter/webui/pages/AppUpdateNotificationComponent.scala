package com.taskadapter.webui.pages

import com.taskadapter.vaadin14shim.{HorizontalLayout, Label}
import com.taskadapter.webui.service.CurrentVersionLoader
import com.taskadapter.webui.{LastVersionLoader, Page, VersionComparator}
import com.vaadin.ui.Component
import com.vaadin.ui.themes.ValoTheme

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
      label.addClassName("important-notification-label")
      layout.add(label)
      val link = WebAppUpdater.createDownloadLink
      layout.add(link)
    }
    layout.setVisible(outdated)
    layout
  }
}
