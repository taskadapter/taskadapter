package com.taskadapter.web.configeditor.server

import com.taskadapter.model.NamedKeyedObject

trait LookupResultListenerScala {
  def notifyDone(objects: Seq[_ <: NamedKeyedObject]): Unit
}