package com.taskadapter.webui

import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.{ClickEvent, Component, ComponentEventListener}
import com.vaadin.flow.component.menubar.MenuBar

object HeaderMenuBuilder {
  def createButton(caption: String, command: Runnable): Component = {
    val menuBar = new MenuBar
//    menuBar.setClassName(ValoTheme.MENUBAR_BORDERLESS)
//    menuBar.addClassName("mybarmenu")
    menuBar.addItem(caption, new ComponentEventListener[ClickEvent[MenuItem]] {
      override def onComponentEvent(event: ClickEvent[MenuItem]): Unit = command.run()
    })
    menuBar
  }
}