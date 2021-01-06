package com.taskadapter.connector.mantis.editor

import com.taskadapter.connector.mantis.MantisConfig
import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.web.configeditor.Editors
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui.Panel

class OtherMantisFieldsPanel(val config: MantisConfig) extends Panel {
  private val DEFAULT_PANEL_CAPTION = "Additional Info"

  val verticalLayout = new VerticalLayout

  setCaption(DEFAULT_PANEL_CAPTION)
  setContent(verticalLayout)
  verticalLayout.setSpacing(true)
  verticalLayout.add(Editors.createFindUsersElement(
    new MethodProperty[java.lang.Boolean](config, "findUserByName")))
}