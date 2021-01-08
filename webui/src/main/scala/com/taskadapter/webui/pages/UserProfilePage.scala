package com.taskadapter.webui.pages

import com.taskadapter.webui.Page.message
import com.taskadapter.webui.user.ChangePasswordDialog
import com.taskadapter.webui.{BasePage, EventTracker, Layout, SessionController}
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.router.Route

@Route(value = Navigator.PROFILE, layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class UserProfilePage() extends BasePage {
  EventTracker.trackPage("user_profile")

  setSpacing(true)
  addLoginInfo()
  val userName = SessionController.getCurrentUserName

  private def addLoginInfo(): Unit = {

    val loginString = message("userProfile.login") + s": $userName"
    add(new Label(loginString))

    val configureSetupsButton = new Button(message("userProfile.configureConnectors"))
    configureSetupsButton.addClickListener(_ => Navigator.setupsList())
    add(configureSetupsButton)

    val button = new Button(message("userProfile.changePassword"))
    button.addClickListener(_ => showChangePasswordDialog())
    add(button)

    val logoutButton = new Button(message("userProfile.logout"))
    logoutButton.addClickListener(_ => SessionController.logout())
    add(logoutButton)
  }

  /**
    * Attempts to change password for the current user.
    */
  private def showChangePasswordDialog() = {
    val selfManagement = SessionController.getUserContext.selfManagement
    ChangePasswordDialog.showDialog(getUI().get(), userName, (oldPassword: String, newPassword: String) =>
      selfManagement.changePassword(oldPassword, newPassword))
  }
}
