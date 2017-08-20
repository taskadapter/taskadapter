package com.taskadapter.connector.common

object ValueTypeResolver {

  def getValueAsFloat(value: Any): Float = {
    if (value.isInstanceOf[String]) {
      value.asInstanceOf[String].toFloat
    } else {
      value.asInstanceOf[Float]
    }
  }

}
