package com.taskadapter.webui.pages

import com.vaadin.flow.component.{Component, HasStyle}
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}

import scala.collection.mutable

class ConfigPanelTabbedSheet extends HorizontalLayout {
  private val buttonArea = new VerticalLayout()
  private val emptyGapArea = new VerticalLayout()
  emptyGapArea.setWidth("10px")
  private val componentArea = new VerticalLayout()
  add(buttonArea)
  add(emptyGapArea)
  add(componentArea)

  private val captionToComponentMap = mutable.Map[String, ReloadableComponent]()
  private val captionToButtonsMap = mutable.Map[String, HasStyle]()
  private val configTabLeftButtonSelected = "configTabLeftButtonSelected"

  def addTab(caption: String, component: ReloadableComponent): Unit = {
    val buttonLayout = new HorizontalLayout()
    buttonLayout.setWidth("150px")
    buttonLayout.setMargin(true)
    buttonLayout.setSpacing(true)
    buttonLayout.addClassName("configTabLeftButton")
    captionToButtonsMap += (caption -> buttonLayout)
    captionToComponentMap += (caption -> component)

    val button = new Label(caption)
    buttonLayout.add(button) // label is not clickable, so have to wrap it into a layout
    buttonLayout.addClickListener(_ => {
      showTab(caption)
    })

    buttonArea.add(buttonLayout)
  }

  def showTab(caption: String): Unit = {
    unselectAllButtons()
    captionToButtonsMap(caption).addClassName(configTabLeftButtonSelected)
    showTab(captionToComponentMap(caption))
  }

  private def unselectAllButtons(): Unit = {
    captionToButtonsMap.values.foreach(b => b.removeClassName(configTabLeftButtonSelected))
  }

  private def showTab(component: ReloadableComponent): Unit = {
    componentArea.removeAll()
    componentArea.add(component.ui())
    component.reload()
  }
}
