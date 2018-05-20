package com.taskadapter.webui.config

import com.taskadapter.config.JsonFactory.gson
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.model.{Assignee, Components, Field, GUser, Reporter}

object DefaultValueResolver {
  val gson = ConfigUtils.createDefaultGson

  val tags: Map[Field[_], FieldTypeTag[_]] = Map(Assignee -> GUserTypeTag,
    Reporter -> GUserTypeTag,
    Components -> SeqStringTypeTag)

  def getTag(field: Field[_]): FieldTypeTag[_] = {
    tags.getOrElse(field, StringTypeTag)
  }
}

abstract sealed class FieldTypeTag[T] {
  def parseDefault(str: String): T

  def serValue(v: T): String = if (v == null) null else gson.toJson(v)

  def deserValue(v: Any): T

  def editableString(v: Any) : String
}

object GUserTypeTag extends FieldTypeTag[GUser] {
  override def parseDefault(str: String): GUser = GUser(null, str, null)

  override def deserValue(v: Any): GUser = {
    if (v == null) {
      return null
    }
    val map = v.asInstanceOf[Map[String, Any]]
    val loginName = map("loginName").asInstanceOf[String]
    parseDefault(loginName)
  }

  override def editableString(v: Any): String = Option(v.asInstanceOf[GUser]).map(_.loginName).getOrElse("")
}

object SeqStringTypeTag extends FieldTypeTag[Seq[String]] {
  override def parseDefault(str: String): Seq[String] = str.split(' ')

  override def deserValue(v: Any): Seq[String] = {
    if (v == null) {
      return null
    }
    val array = v.asInstanceOf[Array[String]]
    array
  }

  override def editableString(v: Any): String = Option(v).toString
}

object StringTypeTag extends FieldTypeTag[String] {
  override def parseDefault(str: String): String = str

  override def deserValue(v: Any): String = {
    if (v == null) {
      return null
    }
    val array = v.asInstanceOf[String]
    array
  }

  override def editableString(v: Any): String = v.asInstanceOf[String]
}