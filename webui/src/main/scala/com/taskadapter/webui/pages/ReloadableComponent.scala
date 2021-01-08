package com.taskadapter.webui.pages

import com.vaadin.flow.component.Component

trait ReloadableComponent {
  def reload(): Unit

  def ui(): Component
}
