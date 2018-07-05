package com.taskadapter.webui.pages

import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.{Label, VerticalLayout}

class ValidationMessagesPanel(caption: String) {
  val layout = new VerticalLayout
  def ui = layout
  def show(messages: Seq[String]) : Unit = {
    layout.removeAllComponents()
    layout.setVisible(messages.nonEmpty)
    if (messages.nonEmpty) {
      val captionLabel = new Label(caption)
      captionLabel.addStyleName("validationPanelCaption")
      layout.addComponent(captionLabel)
      messages.foreach(showMessage)
    }
  }

  def showMessage(message: String) : Unit = {
    val decoratedMessage = s"* $message"
    val errorMessageLabel = new Label(decoratedMessage)
    errorMessageLabel.addStyleName("error-message-label")
    errorMessageLabel.setWidth("600px")
    errorMessageLabel.addStyleName("wrap")
    errorMessageLabel.setContentMode(ContentMode.HTML)
    layout.addComponent(errorMessageLabel)
  }
}