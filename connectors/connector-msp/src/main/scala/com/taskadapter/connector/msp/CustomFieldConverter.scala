package com.taskadapter.connector.msp

/**
  * A custom field value can be anything, e.g. Seq[String] for "label" type loaded from JIRA.
  * That field would have a list of strings. This class converts those values to a single string
  * that can be used in Microsoft Project strings-based fields.
  */
object CustomFieldConverter {

  /**
    * @return empty string if given value is null, otherwise converted value. never returns null
    */
  def getValueAsString(value: Any): String = {
    if (value == null) {
      return ""
    }
    if (value.isInstanceOf[Seq[String]]) {
      val seq = value.asInstanceOf[Seq[String]]
      seq.mkString(" ")
    } else if (value.isInstanceOf[scala.collection.immutable.Seq[String]]) {
      val seq = value.asInstanceOf[scala.collection.immutable.Seq[String]]
      seq.mkString(" ")
    } else {
      value.toString
    }
  }
}
