package com.taskadapter.webui.pages

import com.vaadin.flow.component.Component


trait WizardStep[RES] {
  def getResult: RES

  def ui(config: Any): Component
}
