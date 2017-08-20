package com.taskadapter.webui

import com.taskadapter.data.{State, States}
import com.taskadapter.webui.Page.message
import com.vaadin.ui._

/**
  * Top-level header. Includes logo, "configure", ...
  */
object Header {
  private val MAIN_WIDTH = "900px"

  /**
    * Renders a navigator using provided menu components.
    *
    * @param home  * home click handler.
    * @param menu1 * first menu.
    * @param menu2 * second menu.
    */
  def render(home: Runnable, menu1: Component, menu2: Component, licensed: State[java.lang.Boolean]): Component = {
    val res = new HorizontalLayout
    val internalLayout = new HorizontalLayout
    internalLayout.setWidth(MAIN_WIDTH)
    internalLayout.setSpacing(true)
    res.addComponent(internalLayout)
    res.setComponentAlignment(internalLayout, Alignment.MIDDLE_CENTER)
    res.setSpacing(true)
    res.addStyleName("header-panel")

    // Logo
    val logo = HeaderMenuBuilder.createButton("Task Adapter", home)
    logo.addStyleName("logo")
    internalLayout.addComponent(logo)
    internalLayout.setExpandRatio(logo, 2f)
    internalLayout.setComponentAlignment(logo, Alignment.MIDDLE_LEFT)
    internalLayout.addComponent(menu1)
    internalLayout.setExpandRatio(menu1, 1f)
    internalLayout.setComponentAlignment(menu1, Alignment.MIDDLE_CENTER)
    internalLayout.addComponent(menu2)
    internalLayout.setComponentAlignment(menu2, Alignment.MIDDLE_RIGHT)

    // Trial display.
    val trialLayout = new VerticalLayout
    trialLayout.setSizeFull()
    val trialLabel = new Label(message("header.trialMode"))
    trialLabel.setDescription(message("header.trialModeWillOnlyTransfer"))
    trialLabel.setSizeUndefined()
    trialLabel.addStyleName("trial-mode-label")
    trialLayout.addComponent(trialLabel)
    trialLayout.setComponentAlignment(trialLabel, Alignment.MIDDLE_CENTER)
    States.onValue(licensed, (data: java.lang.Boolean) => trialLayout.setVisible(!data))
    internalLayout.addComponent(trialLayout)
    internalLayout.setExpandRatio(trialLayout, 1f)
    res
  }
}