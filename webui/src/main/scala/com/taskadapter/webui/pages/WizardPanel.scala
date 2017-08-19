package com.taskadapter.webui.pages

import com.taskadapter.webui.Page
import com.vaadin.ui.{HorizontalLayout, Label, ProgressBar, VerticalLayout}

import scala.collection.mutable

class WizardPanel {
  val layout = new VerticalLayout()
  layout.setMargin(true)
  val progressLabel = new Label()
  val progressBar = new ProgressBar(0)
  progressBar.setWidth("400px")
  val stepLayout = new HorizontalLayout()

  layout.addComponents(progressLabel, progressBar)
  layout.addComponent(stepLayout)

  val steps = mutable.Map[Int, WizardStep[_]]()

  def totalSteps = steps.size

  def showStep(step: Int): Unit = {
    progressLabel.setValue(Page.message("newConfig.step", step + "", totalSteps + ""))
    progressBar.setValue(step.toFloat / totalSteps)
    stepLayout.removeAllComponents()
    val previousStep = steps.get(step - 1)
    val config = previousStep.map(_.getResult).getOrElse("")
    val nextStep = steps(step)
    stepLayout.addComponent(nextStep.ui(config))
  }

  def registerStep[C, RES](step: Int, wizardStep: WizardStep[RES]): Unit = {
    steps += (step -> wizardStep)
  }

  def ui = layout

}

