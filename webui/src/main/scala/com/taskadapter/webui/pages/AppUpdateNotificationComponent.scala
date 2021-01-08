package com.taskadapter.webui.pages


import com.taskadapter.webui.service.CurrentVersionLoader
import com.taskadapter.webui.{LastVersionLoader, Page, VersionComparator}
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout



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
