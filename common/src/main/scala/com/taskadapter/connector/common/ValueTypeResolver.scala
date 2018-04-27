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

  def getValueAsString(value: Any): String = {
    value match {
      case null => ""
      case Seq() => ""
      case x: String => x
      case x: Seq[String] => x.head
      case _ => throw new RuntimeException(s"Cannot parse value $value")
    }
  }

}
