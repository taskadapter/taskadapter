package com.taskadapter.webui.config

import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui.{Button, GridLayout, Label, VerticalLayout}

class SetupsListPage(configOperations: ConfigOperations) {
  val layout = new VerticalLayout
  layout.setSpacing(true)
  layout.setWidth(560, PIXELS)

  val ui = layout

  val grid = new GridLayout(3, 5)
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
    setups.foreach { setup =>
      val button = new Button(Page.message("setupsListPage.deleteButton"))
      button.addClickListener(_ => {
        configOperations.deleteConnectorSetup(SetupId(setup.id.get))
        refresh()
      })
      grid.addComponents(new Label(setup.id.get),
      new Label(setup.label),
      button
      )
    }
  }
}
