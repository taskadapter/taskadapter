package com.taskadapter.webui.config

import com.taskadapter.config.JsonFactory.gson
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.model.{Components, DefaultValueResolver, Field, GUser}

object FieldTag {
  val gson = ConfigUtils.createDefaultGson

  val tags: Map[Field[_], FieldTag[_]] = Map(
    Components -> SeqStringTag)

  def getTag(field: Field[_]): FieldTag[_] = {
    tags(field)
  }
}

abstract sealed class FieldTag[T] {
  def serValue(v: T): String = if (v == null) null else gson.toJson(v)

  def deserValue(v: Any): T
}

class GUserTag(field: Field[_]) extends FieldTag[GUser] {
  override def deserValue(v: Any): GUser = {
    if (v == null) {
      return null
    }
    val map = v.asInstanceOf[Map[String, Any]]
    val loginName = map("loginName").asInstanceOf[String]
    DefaultValueResolver.getTag(field).parseDefault(loginName).asInstanceOf[GUser]
  }
}

object SeqStringTag extends FieldTag[Seq[String]] {
  override def deserValue(v: Any): Seq[String] = {
    if (v == null) {
      return null
    }
    val array = v.asInstanceOf[Array[String]]
    array
  }
}

object StringTag extends FieldTag[String] {
  override def deserValue(v: Any): String = {
    if (v == null) {
      return null
    }
    val array = v.asInstanceOf[String]
    array
  }
}