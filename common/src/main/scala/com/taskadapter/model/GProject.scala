package com.taskadapter.model

/**
  * Generic "Project" object. Internal representation for "projects".
  */
case class GProject(id: Long, name: String, key: String, description: String, homepage: String)
  extends NamedKeyedObject {
  override def getName: String = name

  override def getKey: String = key
}
