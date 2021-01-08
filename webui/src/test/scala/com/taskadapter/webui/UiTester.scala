package com.taskadapter.webui

import com.vaadin.flow.component.{Component, HasComponents}


object UiTester {

  def findElement(container: HasComponents, caption: String): Component = {
    val maybeElement = findUiElementOption(container, caption)
    if (maybeElement.isDefined) {
      maybeElement.get
    } else {
      throw new RuntimeException(s"component with caption '$caption' is not found")
    }
  }

  private def findUiElementOption(container: HasComponents, caption: String): Option[Component] = {
    /*val iterator = container.getElementiterator()
    while (iterator.hasNext) {
      val item = iterator.next()
      if (item.getCaption == caption) {
        return Some(item)
      }
      if (item.isInstanceOf[HasComponents]) {
        val inside = findUiElementOption(item.asInstanceOf[HasComponents], caption)
        if (inside.isDefined) {
          return inside
        }
      }
    }*/
    None
  }
}
