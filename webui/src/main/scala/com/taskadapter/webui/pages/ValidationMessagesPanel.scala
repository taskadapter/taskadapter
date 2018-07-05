package com.taskadapter.webui.pages

import com.taskadapter.webui.Page
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.{Button, HorizontalLayout, Label, VerticalLayout}

class ValidationMessagesPanel(caption: String) {
  val layout = new VerticalLayout

  def ui = layout

  def show(errors: Seq[ValidationErrorTextWithProcessor]): Unit = {
    layout.removeAllComponents()
    layout.setVisible(errors.nonEmpty)
    if (errors.nonEmpty) {
      val captionLabel = new Label(caption)
      captionLabel.addStyleName("validationPanelCaption")
      layout.addComponent(captionLabel)
      errors.foreach(showMessage)
    }
  }

  def showMessage(error: ValidationErrorTextWithProcessor): Unit = {
    val row = new HorizontalLayout
    val decoratedMessage = s"* ${error.text}"
    val errorMessageLabel = new Label(decoratedMessage)
    errorMessageLabel.addStyleName("error-message-label")
    errorMessageLabel.setWidth("600px")
    errorMessageLabel.addStyleName("wrap")
    errorMessageLabel.setContentMode(ContentMode.HTML)

    val fixButton = new Button(Page.message("configSummary.fixButtonCaption"))
    fixButton.addClickListener(_ => error.processor.run())
    row.addComponent(errorMessageLabel)
    row.addComponent(fixButton)
    layout.addComponent(row)
  }
}