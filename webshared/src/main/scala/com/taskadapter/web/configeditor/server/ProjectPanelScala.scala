package com.taskadapter.web.configeditor.server

import com.taskadapter.vaadin14shim.GridLayout
import com.taskadapter.connector.definition.exceptions.{BadConfigException, ProjectNotSetException}
import com.taskadapter.model.NamedKeyedObject
import com.taskadapter.web.ExceptionFormatter
import com.taskadapter.web.configeditor.Validatable
import com.taskadapter.web.ui.Grids.addTo
import com.vaadin.ui._

/**
  * "Project info" panel with Project Key, Query Id.
  */
class ProjectPanelScala(projectKeyLabelText: String,
                        projectNameField: TextField,
                        projectKeyField: TextField,
                        projectsLoader: (() => Seq[_ <: NamedKeyedObject]),
                        exceptionFormatter: ExceptionFormatter) extends Panel with Validatable {

  private val DEFAULT_PANEL_CAPTION = "Project Info"
  private val TEXT_AREA_WIDTH = "450px"

  val projectKeyLabel = new Label(projectKeyLabelText)

  setCaption(DEFAULT_PANEL_CAPTION)
  val grid = new GridLayout(4, 2)
  setContent(grid)
  grid.setSpacing(true)

  addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel)
  val projectLabel = new TextField()
  projectLabel.setWidth(TEXT_AREA_WIDTH)
  projectLabel.setEnabled(false)

  addTo(grid, Alignment.MIDDLE_CENTER, projectLabel)
  val showProjectsButton = ButtonFactory.createLookupButton("...", "Show list of available projects on the server.",
    "Select project", "List of projects on the server", projectsLoader, exceptionFormatter,
    result => {
      projectKeyField.setValue(result.getKey)
      projectNameField.setValue(result.getName)
      setVisibleName()
    }
  )
  addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton)
  setVisibleName()

  @throws[BadConfigException]
  override def validate(): Unit = {
    if (projectKeyField.getValue.trim.isEmpty) throw new ProjectNotSetException
  }

  private def setVisibleName(): Unit = {
    val text = if (projectNameField.getValue == null) {
      projectKeyField.getValue
    } else {
      s"${projectNameField.getValue} (${projectKeyField.getValue})"
    }
    projectLabel.setValue(text)
  }
}