package com.taskadapter.model

case class NamedKeyedObjectImpl(key: String, name: String) extends NamedKeyedObject {
  override def getKey: String = key

  override def getName: String = name
}