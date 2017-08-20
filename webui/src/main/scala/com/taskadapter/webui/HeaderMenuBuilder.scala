package com.taskadapter.webui

import com.vaadin.ui.themes.ValoTheme
import com.vaadin.ui.{Component, MenuBar}

object HeaderMenuBuilder {
  def createButton(caption: String, command: Runnable): Component = {
    val menuBar = new MenuBar
    menuBar.setStyleName(ValoTheme.MENUBAR_BORDERLESS)
    menuBar.addStyleName("mybarmenu")
    menuBar.addItem(caption, (selectedItem: MenuBar#MenuItem) => command.run())
    menuBar
  }
}