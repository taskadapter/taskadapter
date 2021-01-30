package com.taskadapter.webui.config

import com.taskadapter.web.PopupDialog
import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.pages.Navigator
import com.taskadapter.webui.{BasePage, ConfigOperations, EventTracker, Layout, Page, SessionController, SetupCategory}
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import com.vaadin.flow.router.Route

@Route(value = Navigator.SETUPS_LIST, layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class SetupsListPage() extends BasePage {
  private val configOps = SessionController.buildConfigOperations()
  private val services = SessionController.getServices

  val introLabel = new Label(Page.message("setupsListPage.intro"))
//  introLabel.addClassName(ValoTheme.LABEL_H3)
  introLabel.setWidth(null)
  val addButton = new Button(Page.message("setupsListPage.addButton"))
  addButton.addClickListener(_ => Navigator.newSetup())

  private val introRow = new HorizontalLayout(introLabel, addButton)
//  introRow.setWidth(850, Sizeable.Unit.PIXELS)
//  introRow.setComponentAlignment(introLabel, Alignment.MIDDLE_LEFT)
//  introRow.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT)
//  introRow.setExpandRatio(introLabel, 1)

  add(introRow)

  val elementsComponent = new VerticalLayout()
  add(elementsComponent)

  refresh()

  private def refresh(): Unit = {
    elementsComponent.removeAll()
    addElements()
  }

  private def addElements(): Unit = {
    val setups = configOps.getConnectorSetups()
    setups.sortBy(x => x.connectorId)
      .foreach { setup =>
        val setupId = SetupId(setup.id.get)
        val editButton = new Button(Page.message("setupsListPage.editButton"))
        editButton.addClickListener(_ => {
          getUI.get().navigate("edit-setup/" + setup.id.get)
        })
        val connectorIdLabel = new Label(setup.connectorId)
//        connectorIdLabel.setData(setupId)
        connectorIdLabel.setWidth(null)
//        connectorIdLabel.addClassName(ValoTheme.LABEL_BOLD)

        val connectorIdLayout = new HorizontalLayout(connectorIdLabel)
        connectorIdLayout.setWidth("200px")
        connectorIdLayout.setHeight("100%")
//        connectorIdLayout.setComponentAlignment(connectorIdLabel, Alignment.MIDDLE_CENTER)

        val setupLabel = new Label(setup.label)
//        setupLabel.addClassName(ValoTheme.LABEL_BOLD)
//        setupLabel.setData(setupId)
        setupLabel.setWidth(null)
        val usedByConfigs = configOps.getConfigIdsUsingThisSetup(setupId)
        val usedByLabel = new Label(Page.message("setupsListPage.usedByConfigs", usedByConfigs.size + ""))
//        usedByLabel.setData(setupId)

        val descriptionLayout = new VerticalLayout(setupLabel, usedByLabel)
        descriptionLayout.setWidth("450px")
        descriptionLayout.setHeight("80px")

        val editLayout = new VerticalLayout(editButton)
//        editLayout.setComponentAlignment(editButton, Alignment.MIDDLE_CENTER)
        editLayout.setWidth("100px")
        editLayout.setHeight("100%")

        val deleteButton = new Button(Page.message("setupsListPage.deleteButton"))
        deleteButton.addClickListener(_ => {
          showDeleteDialog(setupId)
        })
        deleteButton.setEnabled(usedByConfigs.isEmpty)
        val deleteLayout = new VerticalLayout(deleteButton)
//        deleteLayout.setComponentAlignment(deleteButton, Alignment.MIDDLE_CENTER)
        deleteLayout.setWidth("100px")
        deleteLayout.setHeight("100%")

        val row = new HorizontalLayout(connectorIdLayout,
          descriptionLayout,
          editLayout,
          deleteLayout
        )

        elementsComponent.add(row)
      }
  }


  private def showDeleteDialog(setupId: SetupId): Unit = {
    PopupDialog.confirm(Page.message("setupsListPage.confirmDelete.question"),
      () => {
        configOps.deleteConnectorSetup(setupId)
        EventTracker.trackEvent(SetupCategory, "deleted", "")
        refresh()
      })
  }
}
