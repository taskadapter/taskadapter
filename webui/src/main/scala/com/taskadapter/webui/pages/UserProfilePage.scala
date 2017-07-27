package com.taskadapter.webui.pages

import com.taskadapter.webui.Page.message
import com.taskadapter.webui.user.ChangePasswordDialog
import com.vaadin.ui._

class UserProfilePage(userName: String, changePasswordCallback: ChangePasswordDialog.Callback,
                      logoutCallback: Runnable) {
  val ui = new VerticalLayout
  ui.setSpacing(true)
  addLoginInfo()

  private def addLoginInfo(): Unit = {
    val panel = new Panel(message("userProfile.title"))
    ui.addComponent(panel)

    val l = new VerticalLayout
    l.setMargin(true)
    val loginString = message("userProfile.login") + s": $userName"
    l.addComponent(new Label(loginString))

    val button = new Button(message("userProfile.changePassword"))
    button.addClickListener(_ => showChangePasswordDialog())
    l.addComponent(button)

    val logoutButton = new Button(message("userProfile.logout"))
    logoutButton.addClickListener(_ => logoutCallback.run())
    l.addComponent(logoutButton)
    panel.setContent(l)
  }

  /**
    * Attempts to change password for the current user.
    */
  private def showChangePasswordDialog() = {
    ChangePasswordDialog.showDialog(ui.getUI(), userName, changePasswordCallback)
  }
}
