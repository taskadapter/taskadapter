package com.taskadapter.web

import com.taskadapter.webui.Page
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.{Label, Span}
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}

object PopupDialog {
  val YES_LABEL = Page.message("popupDialog.buttonYes")
  val CANCEL_LABEL = Page.message("popupDialog.buttonCancel")

  def confirm(question: String, confirmedAction: () => Unit): Unit = {
    new PopupDialog(/*Page.message("popupDialog.confirmationCaption"),*/
      question, Seq(YES_LABEL, CANCEL_LABEL), (action) => {
        if (action == YES_LABEL) {
          confirmedAction()
        }
      }).open()
  }
}

class PopupDialog(/*caption: String, */question: String, answers: Seq[String], clicked: (String) => Unit)
  extends Dialog {
  setModal(true)
  setCloseOnEsc(true)
  setCloseOnOutsideClick(true)
  // TODO delete "not maximizable" stype

  val view = new VerticalLayout
  view.setSpacing(true)
  view.setMargin(true)
  val label = new Span()
  label.getElement.setProperty("innerHTML", question)
  label.setWidth("300px")
  view.add(label)

//  view.add(new Label("&nbsp;", ContentMode.HTML))
  createButtons(answers)

  add(view)

  private def createButtons(answers: Seq[String]) = {
    val buttonsLayout = new HorizontalLayout
    buttonsLayout.setSpacing(true)
    for (answer <- answers) {
      val button = new Button(answer)
      buttonsLayout.add(button)
      button.addClickListener(event => {
        close()
//        clicked(event.getSource.asInstanceOf[Button].getCaption)
        clicked(event.getSource.asInstanceOf[Button].getText)
      })
      // focus on something in this window so that the window can be closed with ESC
      button.focus()
    }
    view.add(buttonsLayout)
  }
}