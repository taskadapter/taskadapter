package com.taskadapter.vaadin14shim

object Binder {
  def bindField(textField: com.vaadin.flow.component.textfield.TextField, obj: Object, fieldName: String): Unit = {
    val instanceField = BinderJava.getField(obj, fieldName)
    instanceField.setAccessible(true)
    val value = instanceField.get(obj).asInstanceOf[String]
    textField.setValue(value)

    textField.addValueChangeListener((event) => {
      val newValue = event.getValue
      instanceField.set(obj, newValue)
    })

  }


  def bindField(textField: com.vaadin.flow.component.textfield.PasswordField, obj: Object, fieldName: String): Unit = {
    val instanceField = BinderJava.getField(obj, fieldName)
    instanceField.setAccessible(true)
    val value = instanceField.get(obj).asInstanceOf[String]
    textField.setValue(value)

    textField.addValueChangeListener((event) => {
      val newValue = event.getValue
      instanceField.set(obj, newValue)
    })

  }
}
