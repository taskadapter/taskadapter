package com.taskadapter.webui.pages





import com.taskadapter.webui.Page
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}

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
    val decoratedMessage = s"* ${error.getText}"
    val errorMessageLabel = new Label(decoratedMessage)
    errorMessageLabel.addClassName("error-message-label")
    errorMessageLabel.setWidth("600px")
    errorMessageLabel.addClassName("wrap")
//    errorMessageLabel.setContentMode(ContentMode.HTML)

    val fixButton = new Button(Page.message("configSummary.fixButtonCaption"))
    fixButton.addClickListener(_ => error.getProcessor.run())
    row.add(errorMessageLabel)
    row.add(fixButton)
    layout.add(row)
  }
}