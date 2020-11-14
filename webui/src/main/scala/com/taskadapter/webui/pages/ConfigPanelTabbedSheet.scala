package com.taskadapter.webui.pages

import com.vaadin.ui.{Component, HorizontalLayout, Label, Panel, VerticalLayout}

import scala.collection.mutable

class ConfigPanelTabbedSheet {
  val ui = new HorizontalLayout()

  private val buttonArea = new VerticalLayout()
  private val emptyGapArea = new VerticalLayout()
  emptyGapArea.setWidth("10px")
  private val componentArea = new VerticalLayout()
  ui.addComponent(buttonArea)
  ui.addComponent(emptyGapArea)
  ui.addComponent(componentArea)

  private val captionToComponentMap = mutable.Map[String, ReloadableComponent]()
  private val captionToButtonsMap = mutable.Map[String, Component]()
  private val configTabLeftButtonSelected = "configTabLeftButtonSelected"

  def addTab(caption: String, component: ReloadableComponent): Unit = {
    val layout = new HorizontalLayout()
    layout.setWidth("150px")
    layout.setMargin(true)
    layout.setSpacing(true)
    layout.addStyleName("configTabLeftButton")
    captionToButtonsMap += (caption -> layout)
    captionToComponentMap += (caption -> component)

    val panel = new Panel()
    val button = new Label(caption)
    layout.addComponent(button) // label is not clickable, so have to wrap it into a layout
    layout.addLayoutClickListener(_ => {
      showTab(caption)
    })

    panel.setContent(layout)

    buttonArea.addComponent(panel)
  }

  def showTab(caption: String): Unit = {
    unselectAllButtons()
    captionToButtonsMap(caption).addStyleName(configTabLeftButtonSelected)
    showTab(captionToComponentMap(caption))
  }

  private def unselectAllButtons(): Unit = {
    captionToButtonsMap.values.foreach(b => b.removeStyleName(configTabLeftButtonSelected))
  }

  private def showTab(component: ReloadableComponent): Unit = {
    componentArea.removeAllComponents()
    componentArea.addComponent(component.ui())
    component.reload()
  }
}
