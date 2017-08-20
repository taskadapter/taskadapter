package com.taskadapter.connector

/**
  * @param typeName like 'Date', 'Float', 'String'. not real class names.
  * @param name field name
  */
case class Field(typeName: String, name: String)

object Field {
  def apply(fieldName: String): Field = {
    Field("String", fieldName)
  }

  def date(fieldName: String): Field = {
    Field("Date", fieldName)
  }

  def float(fieldName: String): Field = {
    Field("Float", fieldName)
  }

  def user(fieldName: String): Field = {
    Field("GUser", fieldName)
  }

  def integer(fieldName: String): Field = {
    Field("Integer", fieldName)
  }
}
