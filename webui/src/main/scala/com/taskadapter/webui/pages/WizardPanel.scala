package com.taskadapter.webui.pages

import com.taskadapter.vaadin14shim.{HorizontalLayout, VerticalLayout, Label}
import com.taskadapter.webui.Page
import com.vaadin.ui.{ProgressBar}

import scala.collection.mutable

class WizardPanel {
  val layout = new VerticalLayout()
  layout.setMargin(true)
  val progressLabel = new Label()
  val progressBar = new ProgressBar(0)
  progressBar.setWidth("400px")
  val stepLayout = new HorizontalLayout()

  layout.addComponents(progressLabel, progressBar)
  layout.add(stepLayout)

  val steps = mutable.Map[Int, WizardStep[_]]()

  def totalSteps = steps.size

  def showStep(step: Int): Unit = {
    progressLabel.setText(Page.message("newConfig.step", step + "", totalSteps + ""))
    progressBar.setValue(step.toFloat / totalSteps)
    stepLayout.removeAll()
    val previousStep = steps.get(step - 1)
    val config = previousStep.map(_.getResult).getOrElse("")
    val nextStep = steps(step)
    stepLayout.add(nextStep.ui(config))
  }

  def registerStep[C, RES](wizardStep: WizardStep[RES]): Unit = {
    val stepNumber = steps.size + 1
    steps += (stepNumber -> wizardStep)
  }

  def ui = layout

}

