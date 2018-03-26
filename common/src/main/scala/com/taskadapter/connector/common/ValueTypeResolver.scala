package com.taskadapter.connector.common

object ValueTypeResolver {

  def getValueAsFloat(value: Any): Float = {
    if (value.isInstanceOf[String]) {
      value.asInstanceOf[String].toFloat
    } else if (value.isInstanceOf[Int]) {
      value.asInstanceOf[Int].toFloat
    } else {
      value.asInstanceOf[Float]
    }
  }

  def getValueAsInt(value: Any): Int = {
    if (value.isInstanceOf[String]) {
      value.asInstanceOf[String].toInt
    } else if (value.isInstanceOf[Float]) {
      value.asInstanceOf[Float].toInt
    } else {
      value.asInstanceOf[Int]
    }
  }

}
