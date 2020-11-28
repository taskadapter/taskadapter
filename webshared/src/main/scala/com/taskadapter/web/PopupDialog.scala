package com.taskadapter.web

import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.webui.Page
import com.vaadin.event.ShortcutAction
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._

object PopupDialog {
  val YES_LABEL = Page.message("popupDialog.buttonYes")
  val CANCEL_LABEL = Page.message("popupDialog.buttonCancel")

  def confirm(question: String, confirmedAction: () => Unit): PopupDialog = {
    new PopupDialog(Page.message("popupDialog.confirmationCaption"),
      question, Seq(YES_LABEL, CANCEL_LABEL), (action) => {
        if (action == YES_LABEL) {
          confirmedAction()
        }
      })
  }
}

class PopupDialog(caption: String, question: String, answers: Seq[String], clicked: (String) => Unit)
  extends Window(caption) {
  setModal(true)
  setCloseShortcut(ShortcutAction.KeyCode.ESCAPE)
  addStyleName("not-maximizable-window")

  val view = new VerticalLayout
  view.setSpacing(true)
  view.setMargin(true)
  val label = new Label(question, ContentMode.HTML)
  label.setWidth("300px")
  view.add(label)
  view.add(new Label("&nbsp;", ContentMode.HTML))
  createButtons(answers)
  setContent(view)

  private def createButtons(answers: Seq[String]) = {
    val buttonsLayout = new HorizontalLayout
    buttonsLayout.setSpacing(true)
    for (answer <- answers) {
      val button = new Button(answer)
      buttonsLayout.add(button)
      button.addClickListener(event => {
        getUI.removeWindow(this)
        clicked(event.getSource.asInstanceOf[Button].getCaption)
      })
      // focus on something in this window so that the window can be closed with ESC
      button.focus()
    }
    view.add(buttonsLayout)
  }
}