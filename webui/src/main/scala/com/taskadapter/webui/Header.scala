package com.taskadapter.webui

import com.taskadapter.data.{State, States}
import com.taskadapter.webui.Page.message
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}

/**
  * Top-level header. Includes logo, "configure", ...
  */
object Header {

  /**
    * Renders a navigator using provided menu components.
    *
    * @param home  * home click handler.
    * @param menu1 * first menu.
    */
  def render(home: Runnable, menu1: Component, licensed: State[java.lang.Boolean]): Component = {
    val res = new HorizontalLayout
    val internalLayout = new HorizontalLayout
    internalLayout.setWidth("100%")
    internalLayout.setSpacing(true)
    res.add(internalLayout)
//    res.setComponentAlignment(internalLayout, Alignment.MIDDLE_CENTER)
    res.setSpacing(true)
    res.addClassName("header-panel")

    // Logo
    val logo = HeaderMenuBuilder.createButton("Task Adapter", home)
//    logo.addClassName("logo")
    internalLayout.add(logo)
//    internalLayout.setExpandRatio(logo, 2f)
//    internalLayout.setComponentAlignment(logo, Alignment.MIDDLE_LEFT)
    internalLayout.add(menu1)

    // Trial display.
    val trialLayout = new VerticalLayout
    trialLayout.setSizeFull()
    val trialLabel = new Label(message("header.trialMode"))
//    trialLabel.setDescription(message("header.trialModeWillOnlyTransfer"))
    trialLabel.setSizeUndefined()
    trialLabel.addClassName("trial-mode-label")
    trialLayout.add(trialLabel)
//    trialLayout.setComponentAlignment(trialLabel, Alignment.MIDDLE_CENTER)
    States.onValue(licensed, (data: java.lang.Boolean) => trialLayout.setVisible(!data))
    internalLayout.add(trialLayout)
//    internalLayout.setExpandRatio(trialLayout, 1f)
    res
  }
}