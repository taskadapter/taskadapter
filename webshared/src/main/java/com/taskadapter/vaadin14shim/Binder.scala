package com.taskadapter.vaadin14shim

import com.vaadin.data.Property
import com.vaadin.ui.AbstractTextField

object Binder {
  def bindField(textField: AbstractTextField, obj: Object, fieldName: String): Unit = {
    val instanceField = BinderJava.getField(obj, fieldName)
    instanceField.setAccessible(true)
    val value = instanceField.get(obj).asInstanceOf[String]
    textField.setValue(value)

    textField.addValueChangeListener((event: Property.ValueChangeEvent) => {
      val newValue = event.getProperty.getValue
      instanceField.set(obj, newValue)
    })

  }
}
