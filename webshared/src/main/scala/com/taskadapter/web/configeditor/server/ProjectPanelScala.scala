package com.taskadapter.web.configeditor.server

import com.taskadapter.connector.definition.exceptions.{BadConfigException, ProjectNotSetException}
import com.taskadapter.model.NamedKeyedObject
import com.taskadapter.web.ExceptionFormatter
import com.taskadapter.web.configeditor.EditorUtil.textInput
import com.taskadapter.web.configeditor.Validatable
import com.taskadapter.web.ui.Grids.addTo
import com.vaadin.data.Property
import com.vaadin.ui._
import org.slf4j.LoggerFactory

/**
  * "Project info" panel with Project Key, Query Id.
  */
class ProjectPanelScala(projectKeyProperty: Property[String],
                        projectsLoader: (() => Seq[_ <: NamedKeyedObject]),
                        exceptionFormatter: ExceptionFormatter) extends Panel with Validatable {

  private val logger = LoggerFactory.getLogger(classOf[ProjectPanelScala])
  private val DEFAULT_PANEL_CAPTION = "Project Info"
  private val TEXT_AREA_WIDTH = "120px"

  val projectKeyLabel = new Label("Project key:")

  setCaption(DEFAULT_PANEL_CAPTION)
  val grid = new GridLayout(4, 2)
  setContent(grid)
  grid.setSpacing(true)

  addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel)
  val projectKey = textInput(projectKeyProperty, TEXT_AREA_WIDTH)
  addTo(grid, Alignment.MIDDLE_CENTER, projectKey)
  projectKey.setNullRepresentation("")
  val showProjectsButton = ButtonFactory.createLookupButton("...", "Show list of available projects on the server.",
    "Select project", "List of projects on the server", projectsLoader, projectKeyProperty, false, exceptionFormatter)
  addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton)

  def setProjectKeyLabel(text: String): Unit = projectKeyLabel.setValue(text)

  @throws[BadConfigException]
  override def validate(): Unit = {
    if (projectKey.getValue.trim.isEmpty) throw new ProjectNotSetException
  }
}