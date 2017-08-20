package com.taskadapter.webui

import com.taskadapter.web.data.Messages

object Page {
  private val BUNDLE_NAME = "com.taskadapter.webui.data.messages"
  val MESSAGES = new Messages(BUNDLE_NAME)

  def message(key: String): String = MESSAGES.get(key)

  //  @annotation.varargs is a hint to generate a Java version of the method which takes an array instead of a Seq.
  @annotation.varargs def message(key: String, argument: String*): String = MESSAGES.format(key, argument)

  def message(key: String, argument: String): String = MESSAGES.format(key, argument)
  def message(key: String, argument1: String, argument2: String): String = MESSAGES.format(key, argument1, argument2)
}
