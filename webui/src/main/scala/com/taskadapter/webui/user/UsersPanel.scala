package com.taskadapter.webui.user

import com.taskadapter.auth.{AuthException, AuthorizedOperations, CredentialsManager}
import com.taskadapter.data.{MutableState, States}
import com.taskadapter.license.License
import com.taskadapter.web.event.{EventCategory, EventTracker}
import com.taskadapter.web.{InputDialog, PopupDialog}
import com.taskadapter.webui.Page
import com.taskadapter.webui.Page.message
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.{Hr, Label}
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.{Html, Text}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class UsersPanel(credentialsManager: CredentialsManager, authorizedOperations: AuthorizedOperations, license: License)
  extends VerticalLayout {

  setWidth("500px")

  val logger = LoggerFactory.getLogger(classOf[UsersPanel])
  val captionLabel = new Html("<b>" + Page.MESSAGES.get("users.title") + "</b>")

  setMargin(true)
  setSpacing(true)
  val errorLabel = new Label
  errorLabel.setVisible(false)
  val statusLabel = new Text("asdasdasdasdsad")

  val usersLayout = new FormLayout()
  usersLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("40em", 1),
    new FormLayout.ResponsiveStep("30em", 2),
    new FormLayout.ResponsiveStep("20em", 3));

  val users = credentialsManager.listUsers.asScala
  val numUsers = new MutableState[Int](users.size)
  val addUserButton = new Button(message("users.addUser"))
  addUserButton.addClickListener(_ => startCreateUserProcess())
  States.onValue(numUsers, (data: Int) => applyLicenseRestriction(data))
  refreshUsers(users)

  add(new Hr(),
    captionLabel, errorLabel, statusLabel, usersLayout, addUserButton)

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
    PopupDialog.confirm(message("users.deleteUser", userLoginName),
      () => {
        deleteUser(userLoginName)
      })
  }

  private def deleteUser(userLoginName: String): Unit = {
    try {
      credentialsManager.removeUser(userLoginName)
      EventTracker.trackEvent(EventCategory.UserCategory, "deleted", "")
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
        dialog.close()
        reloadUsers()
        // TODO would be nice to show "login name is empty" warning otherwise...}
      }
    })
    dialog.open()
  }

  private def createUser(login: String, password: String) = {
    try {
      credentialsManager.savePrimaryAuthToken(login, password)
      EventTracker.trackEvent(EventCategory.UserCategory, "created", "")
    } catch {
      case e: AuthException =>
        logger.error("User initiation error", e)
        throw new RuntimeException(e)
    }
  }

  private def startSetPasswordProcess(userLoginName: String) = {
    InputDialog.showSecret(message("users.changePassword", userLoginName),
      message("users.newPassword"),
      (newPassword: String) =>
        try {
          credentialsManager.savePrimaryAuthToken(userLoginName, newPassword)
          logger.info("Saved password for user " + userLoginName)
        }
        catch {
          case e: AuthException =>
            logger.error("Change password error", e)
            throw new RuntimeException(e)
        })
  }
}