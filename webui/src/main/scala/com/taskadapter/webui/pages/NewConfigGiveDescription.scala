package com.taskadapter.webui.pages

import com.taskadapter.webui.Page.message
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.TextField

class NewConfigGiveDescription(saveClicked: String => Unit) extends WizardStep[String] {
  var result = ""
  val layout = new FormLayout()
  layout.setResponsiveSteps(
    new FormLayout.ResponsiveStep("50em", 1))

  val descriptionTextField = new TextField(message("createConfigPage.description"))
  descriptionTextField.setRequired(true)
  descriptionTextField.setWidth("400px")

  val createButton = new Button(message("createConfigPage.create"))
  createButton.addClickListener(_ => saveClicked(descriptionTextField.getValue))

  createButton.setEnabled(descriptionTextField.getValue.nonEmpty)
  descriptionTextField.addValueChangeListener(e =>
    createButton.setEnabled(e.getValue.asInstanceOf[String].nonEmpty))

  layout.add(descriptionTextField,
    createButton)

  override def getResult: String = result

  override def ui(config: Any) = layout
}
