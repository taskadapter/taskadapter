package com.taskadapter.webui.pages

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
    addComponent(panel)

    val l = new VerticalLayout
    l.setMargin(true)
    l.setSpacing(true)
    val loginString = message("userProfile.login") + s": $userName"
    l.addComponent(new Label(loginString))

    val configureSetupsButton = new Button(message("userProfile.configureConnectors"))
    configureSetupsButton.addClickListener(_ => EventBusImpl.post(ShowSetupsListPageRequested()))
    l.addComponent(configureSetupsButton)

    val button = new Button(message("userProfile.changePassword"))
    button.addClickListener(_ => showChangePasswordDialog())
    l.addComponent(button)

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
