package com.taskadapter.webui.pages

import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.vaadin14shim.Label
import com.taskadapter.vaadin14shim.Button
import com.taskadapter.webui.Page
import com.vaadin.shared.ui.label.ContentMode

class ValidationMessagesPanel(caption: String) {
  val layout = new VerticalLayout

  def ui = layout

  def show(errors: Seq[ValidationErrorTextWithProcessor]): Unit = {
    layout.removeAll()
    layout.setVisible(errors.nonEmpty)
    if (errors.nonEmpty) {
      val captionLabel = new Label(caption)
      captionLabel.addClassName("validationPanelCaption")
      layout.add(captionLabel)
      errors.foreach(showMessage)
    }
  }

  def showMessage(error: ValidationErrorTextWithProcessor): Unit = {
    val row = new HorizontalLayout
    val decoratedMessage = s"* ${error.text}"
    val errorMessageLabel = new Label(decoratedMessage)
    errorMessageLabel.addClassName("error-message-label")
    errorMessageLabel.setWidth("600px")
    errorMessageLabel.addClassName("wrap")
    errorMessageLabel.setContentMode(ContentMode.HTML)

    val fixButton = new Button(Page.message("configSummary.fixButtonCaption"))
    fixButton.addClickListener(_ => error.processor.run())
    row.addComponent(errorMessageLabel)
    row.addComponent(fixButton)
    layout.add(row)
  }
}