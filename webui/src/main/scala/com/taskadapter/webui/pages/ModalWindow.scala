package com.taskadapter.webui.pages

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dialog.Dialog

object ModalWindow {

  def showDialog(component: Component): Dialog = {
    val dialog = new Dialog()
    dialog.setModal(true)
    dialog.setCloseOnEsc(true)
    dialog.setCloseOnOutsideClick(true)
    dialog.add(component)
    dialog.open()
    dialog
  }
}
