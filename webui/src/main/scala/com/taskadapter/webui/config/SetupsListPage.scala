package com.taskadapter.webui.config

import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._

class SetupsListPage(configOperations: ConfigOperations,
                     showEditSetup: (SetupId) => Unit,
                     showNewSetup: () => Unit) {
  val layout = new VerticalLayout
  layout.setSpacing(true)
  layout.setWidth(560, PIXELS)

  private val addButton = new Button(Page.message("setupsListPage.addButton"))
  addButton.addClickListener(_ => showNewSetup())

  layout.addComponent(addButton)

  val ui = layout

  val grid = new GridLayout(4, 5)
  grid.setSpacing(true)
  grid.setWidth("560px")
  layout.addComponent(grid)

  refresh()

  private def refresh(): Unit = {
    grid.removeAllComponents()
    addElements()
  }

  private def addElements(): Unit = {
    val setups = configOperations.getConnectorSetups()
    setups.sortBy(x => x.connectorId)
      .foreach { setup =>
      val deleteButton = new Button(Page.message("setupsListPage.deleteButton"))
      val editButton = new Button(Page.message("setupsListPage.editButton"))
      val setupId = SetupId(setup.id.get)
      deleteButton.addClickListener(_ => {
        configOperations.deleteConnectorSetup(setupId)
        refresh()
      })

      editButton.addClickListener(_ => {
        showEditSetup(setupId)
      })
      val connectorIdLabel = new Label(setup.connectorId)
      connectorIdLabel.setData(setupId)
      val setupLabel = new Label(setup.label)
      setupLabel.setData(setupId)
      grid.addComponents(connectorIdLabel,
        setupLabel,
        editButton,
        deleteButton
      )
    }
  }

}
