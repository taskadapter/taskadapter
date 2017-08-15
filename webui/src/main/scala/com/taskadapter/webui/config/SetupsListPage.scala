package com.taskadapter.webui.config

import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme

class SetupsListPage(configOperations: ConfigOperations,
                     showEditSetup: (SetupId) => Unit,
                     showNewSetup: () => Unit) {
  val layout = new VerticalLayout
  layout.setSpacing(true)
  layout.setWidth(560, PIXELS)

  val introLabel = new Label(Page.message("setupsListPage.intro"))
  introLabel.addStyleName(ValoTheme.LABEL_H3)
  val addButton = new Button(Page.message("setupsListPage.addButton"))
  addButton.addClickListener(_ => showNewSetup())

  private val introRow = new HorizontalLayout(introLabel, addButton)
  introRow.setComponentAlignment(introLabel, Alignment.MIDDLE_LEFT)
  introRow.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT)
  introRow.setWidth("100%")
  introRow.setExpandRatio(introLabel, 1)

  layout.addComponent(introRow)

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
