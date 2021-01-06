package com.taskadapter.webui.pages

import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.web.event.{EventBusImpl, ShowSetupsListPageRequested}
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.user.ChangePasswordDialog
import com.taskadapter.webui.{BasePage, EventTracker, SessionController}
import com.vaadin.ui._

class UserProfilePage() extends BasePage {
  EventTracker.trackPage("user_profile")

  setSpacing(true)
  addLoginInfo()
  val userName = SessionController.getCurrentUserName

  private def addLoginInfo(): Unit = {
    val panel = new Panel(message("userProfile.title"))
    add(panel)

    val l = new VerticalLayout
    l.setMargin(true)
    l.setSpacing(true)
    val loginString = message("userProfile.login") + s": $userName"
    l.add(new Label(loginString))

    val configureSetupsButton = new Button(message("userProfile.configureConnectors"),
      _ => EventBusImpl.post(ShowSetupsListPageRequested()))
    l.add(configureSetupsButton)

    val button = new Button(message("userProfile.changePassword"))
    button.addClickListener(_ => showChangePasswordDialog())
    l.add(button)

    val logoutButton = new Button(message("userProfile.logout"))
    logoutButton.addClickListener(_ => SessionController.logout())
    l.addComponent(logoutButton)
    panel.setContent(l)
  }

  /**
    * Attempts to change password for the current user.
    */
  private def showChangePasswordDialog() = {
    val selfManagement = SessionController.getUserContext.selfManagement
    ChangePasswordDialog.showDialog(getUI(), userName, (oldPassword: String, newPassword: String) =>
      selfManagement.changePassword(oldPassword, newPassword))
  }
}
