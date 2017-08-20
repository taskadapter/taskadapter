package com.taskadapter.webui

import com.taskadapter.webui.Page.message
import com.taskadapter.webui.license.LicenseFacade
import com.taskadapter.webui.pages.{LoginPage, SupportPage}
import com.taskadapter.webui.service.Preservices
import com.vaadin.ui.{Alignment, Component, HorizontalLayout}

/**
  * Pageset available to all users.
  */
object WelcomePageset {
  def createPageset(services: Preservices, tracker: Tracker, callback: LoginPage.Callback): Component = {
    val ctl = new WelcomePageset(services, tracker, callback)
    ctl.showLogin()
    ctl.ui
  }
}

class WelcomePageset(services: Preservices, tracker: Tracker, callback: LoginPage.Callback) {
  val licenseFacade = new LicenseFacade(services.licenseManager)
  def createMenu = new HorizontalLayout(HeaderMenuBuilder.createButton(message("headerMenu.support"), () => showSupport()))
  val header = Header.render(() => showLogin(), createMenu, new HorizontalLayout, licenseFacade.isLicensed)
  val currentComponentArea = new HorizontalLayout

  val ui = TAPageLayout.layoutPage(header, currentComponentArea)

  private def showLogin(): Unit = {
    tracker.trackPage("login")
    applyUI(LoginPage.createUI(callback))
  }

  private def showSupport(): Unit = {
    tracker.trackPage("support")
    applyUI(SupportPage.render(services.currentTaskAdapterVersion, licenseFacade, tracker))
  }

  private def applyUI(ui: Component): Unit = {
    currentComponentArea.removeAllComponents()
    ui.setSizeUndefined()
    currentComponentArea.addComponent(ui)
    currentComponentArea.setComponentAlignment(ui, Alignment.TOP_LEFT)
  }
}