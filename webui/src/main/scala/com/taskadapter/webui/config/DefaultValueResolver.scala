package com.taskadapter.webui.config

import com.taskadapter.model.{Assignee, Field, GUser, Reporter}

object DefaultValueResolver {

  /**
    * Get type-safe value specific for this [[Field]] class given a string value
    */
  def resolveDefaultValueWithProperType[T](field: Field[T], value: String): T = {
    val resolved = if (value == "") null else field match {
      case Assignee | Reporter => GUser(null, value, null)
      case _ => value
    }
    resolved.asInstanceOf[T]
  }
}
