package com.taskadapter.webui.pages

import com.vaadin.ui.{Component, UI, Window}

object ModalWindow {

  def showWindow(ui: UI): Window = {
    val newWindow = new Window()
    newWindow.center()
    newWindow.setModal(true)
    ui.addWindow(newWindow)
    newWindow.focus()
    newWindow
  }

  def show(ui: UI, component: Component): Unit = {
    val newWindow = new Window()
    newWindow.setContent(component)
    newWindow.center()
    newWindow.setModal(true)
    ui.addWindow(newWindow)
    newWindow.focus()
  }

}
