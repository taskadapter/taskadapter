package com.taskadapter.webui.pages


import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.vaadin14shim.GridLayout
import com.vaadin.ui.{Component, Label, Panel}

import scala.collection.mutable

class ConfigPanelTabbedSheet {
  val ui = new HorizontalLayout()

  private val buttonArea = new VerticalLayout()
  private val emptyGapArea = new VerticalLayout()
  emptyGapArea.setWidth("10px")
  private val componentArea = new VerticalLayout()
  ui.add(buttonArea)
  ui.add(emptyGapArea)
  ui.add(componentArea)

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
    layout.add(button) // label is not clickable, so have to wrap it into a layout
    layout.addLayoutClickListener(_ => {
      showTab(caption)
    })

    panel.setContent(layout)

    buttonArea.add(panel)
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
    componentArea.removeAll()
    componentArea.add(component.ui())
    component.reload()
  }
}
