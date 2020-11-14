package com.taskadapter.webui.pages

import com.vaadin.ui.Component

trait ReloadableComponent {
  def reload(): Unit

  def ui(): Component
}
