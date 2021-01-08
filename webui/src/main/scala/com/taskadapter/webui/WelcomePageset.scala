package com.taskadapter.webui

import com.taskadapter.webui.Page.message
import com.taskadapter.webui.license.LicenseFacade
import com.taskadapter.webui.pages.{AppUpdateNotificationComponent, LoginPage, SupportPage}
import com.taskadapter.webui.service.Preservices
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class WelcomePageset(services: Preservices) {
  val licenseFacade = new LicenseFacade(services.licenseManager)
  def createMenu = new HorizontalLayout(HeaderMenuBuilder.createButton(message("headerMenu.support"), () =>{}))
//  val header = Header.render(() => showLogin(), createMenu, /*new HorizontalLayout,*/ licenseFacade.isLicensed)
  val currentComponentArea = new HorizontalLayout

//  val ui = TAPageLayout.layoutPage(header, new AppUpdateNotificationComponent, currentComponentArea)

}