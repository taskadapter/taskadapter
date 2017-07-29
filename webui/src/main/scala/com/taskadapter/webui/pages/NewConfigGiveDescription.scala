package com.taskadapter.webui.pages

import com.taskadapter.webui.Page.message
import com.vaadin.ui.{Alignment, Button, GridLayout, TextField}

class NewConfigGiveDescription(saveClicked: String => Unit) {
  val grid = new GridLayout(2, 2)
  grid.setSpacing(true)
  grid.setMargin(true)

  val descriptionTextField = new TextField(message("createConfigPage.description"))
  descriptionTextField.setInputPrompt(message("createConfigPage.optional"))
  descriptionTextField.setWidth("100%")
  grid.addComponent(descriptionTextField, 0, 0)
  grid.setComponentAlignment(descriptionTextField, Alignment.MIDDLE_CENTER)

  def ui = grid

  val saveButton = new Button(message("createConfigPage.create"))
  saveButton.addClickListener(_ => saveClicked(descriptionTextField.getValue))
  grid.addComponent(saveButton, 0, 1)
  grid.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT)

  /**
    * @return None if no error was found
    */
  /*
    private def validate(): Option[String] = {
      if (connector1.getValue == null) {
        return Some(message("createConfigPage.pleaseSelectSystem1"))
      }

      val info1Error = if (connector1Panel.isDefined) connector1Panel.get.validate() else None
      if (info1Error.isDefined) {
        return info1Error
      }

      if (connector2.getValue == null) {
        return Some(message("createConfigPage.pleaseSelectSystem2"))
      }
      val info2Error = if (connector2Panel.isDefined) connector2Panel.get.validate() else None
      if (info2Error.isDefined) {
        return info2Error
      }
      None
    }
  */


}
