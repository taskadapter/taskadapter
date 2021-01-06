package com.taskadapter.vaadin14shim

import com.vaadin.data.Property

object Binder {
  def bindField(textField: TextField, obj: Object, fieldName: String): Unit = {
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
