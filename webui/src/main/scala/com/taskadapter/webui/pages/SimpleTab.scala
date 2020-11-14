package com.taskadapter.webui.pages

import com.vaadin.ui.Component

class SimpleTab(component: Component) extends ReloadableComponent {
  override def reload(): Unit = {}

  override def ui(): Component = component
}
