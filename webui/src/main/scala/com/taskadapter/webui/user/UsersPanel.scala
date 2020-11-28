package com.taskadapter.webui.user


import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.Label
import com.taskadapter.vaadin14shim.TextField
import com.taskadapter.vaadin14shim.GridLayout
import com.taskadapter.auth.{AuthException, AuthorizedOperations, CredentialsManager}
import com.taskadapter.data.{MutableState, States}
import com.taskadapter.license.License
import com.taskadapter.web.{InputDialog, PopupDialog}
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.{EventTracker, UserCategory}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class UsersPanel(credentialsManager: CredentialsManager, authorizedOperations: AuthorizedOperations, license: License) {

  val logger = LoggerFactory.getLogger(classOf[UsersPanel])
  val COLUMNS_NUMBER = 3

  val ui = new Panel(message("users.title"))
  val view = new VerticalLayout
  view.setMargin(true)
  view.setSpacing(true)
  ui.setContent(view)
  val errorLabel = new Label
  errorLabel.addClassName(ValoTheme.LABEL_FAILURE)
  errorLabel.setVisible(false)
  view.addComponent(errorLabel)
  val statusLabel = new Label
  view.add(statusLabel)
  val usersLayout = new GridLayout
  usersLayout.setColumns(COLUMNS_NUMBER)
  usersLayout.setSpacing(true)
  view.add(usersLayout)
  val users = credentialsManager.listUsers.asScala
  val numUsers = new MutableState[Int](users.size)
  val addUserButton = new Button(message("users.addUser"))
  addUserButton.addClickListener(_ => startCreateUserProcess())
  view.add(addUserButton)
  States.onValue(numUsers, (data: Int) => applyLicenseRestriction(data))
  refreshUsers(users)

  private def reloadUsers(): Unit = {
    refreshUsers(credentialsManager.listUsers.asScala)
  }

  private def applyLicenseRestriction(currentNumberOfUsersCreatedInSystem: Int) = {
    addUserButton.setEnabled(license != null
      && currentNumberOfUsersCreatedInSystem < license.getUsersNumber
      && authorizedOperations.canAddUsers)
    if (license == null) {
      statusLabel.setText(message("users.cantAddUsersUntilLicenseInstalled"))
    } else if (license.getUsersNumber <= currentNumberOfUsersCreatedInSystem) {
      statusLabel.setText(message("users.maximumUsersNumberReached"))
    } else {
      statusLabel.setText("")
    }
  }

  private def refreshUsers(users: Seq[String]): Unit = {
    usersLayout.removeAll()
    users.sorted.foreach(u => addUserToPanel(u))
    numUsers.set(users.size)
  }

  private def addUserToPanel(userLoginName: String) = {
    val userLoginLabel = new Label(userLoginName)
    userLoginLabel.addClassName("userLoginLabelInUsersPanel")
    usersLayout.add(userLoginLabel)
    if (authorizedOperations.canChangePasswordFor(userLoginName)) usersLayout.add(createSetPasswordButton(userLoginName))
    else usersLayout.add(new Label(""))
    if (authorizedOperations.canDeleteUser(userLoginName)) usersLayout.add(createDeleteButton(userLoginName))
    else usersLayout.add(new Label(""))
  }

  private def createSetPasswordButton(userLoginName: String) = {
    val setPasswordButton = new Button(message("users.setPassword"))
    setPasswordButton.addClickListener(_ => startSetPasswordProcess(userLoginName))
    setPasswordButton
  }

  private def createDeleteButton(userLoginName: String) = {
    val deleteButton = new Button(message("button.delete"))
    deleteButton.addClickListener(_ => startDeleteProcess(userLoginName))
    deleteButton
  }

  private def startDeleteProcess(userLoginName: String) = {
    val deleteText = message("button.delete")
    val messageDialog = PopupDialog.confirm(message("users.deleteUser", userLoginName),
      () => {
        deleteUser(userLoginName)
      })
    messageDialog.setWidth(200, PIXELS)
    ui.getUI.addWindow(messageDialog)
  }

  private def deleteUser(userLoginName: String): Unit = {
    try {
      credentialsManager.removeUser(userLoginName)
      EventTracker.trackEvent(UserCategory, "deleted", "")
    } catch {
      case e: AuthException =>
        showError(message("users.error.cantDeleteUser", e.toString))
    }
    reloadUsers()
  }

  private def showError(message: String) = {
    errorLabel.setText(message)
    errorLabel.setVisible(true)
  }

  private def startCreateUserProcess() = {
    val dialog = new CreateUserDialog()
    dialog.addOKListener(() => {
      val loginName = dialog.getLogin
      if (!loginName.isEmpty) {
        createUser(loginName, dialog.getPassword)
        ui.getUI.removeWindow(dialog)
        reloadUsers()
        // TODO would be nice to show "login name is empty" warning otherwise...}
      }
    })
    ui.getUI.addWindow(dialog)
  }

  private def createUser(login: String, password: String) = {
    try {
      credentialsManager.savePrimaryAuthToken(login, password)
      EventTracker.trackEvent(UserCategory, "created", "")
    } catch {
      case e: AuthException =>
        logger.error("User initiation error", e)
        throw new RuntimeException(e)
    }
  }

  private def startSetPasswordProcess(userLoginName: String) = {
    val inputDialog = new InputDialog(message("users.changePassword", userLoginName), message("users.newPassword"),
      (newPassword: String) =>
        try credentialsManager.savePrimaryAuthToken(userLoginName, newPassword)
        catch {
          case e: AuthException =>
            logger.error("Change password error", e)
            throw new RuntimeException(e)
        })
    inputDialog.setPasswordMode()
    ui.getUI.addWindow(inputDialog)
  }
}