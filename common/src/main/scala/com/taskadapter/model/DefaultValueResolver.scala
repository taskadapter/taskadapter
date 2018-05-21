package com.taskadapter.model

import java.text.SimpleDateFormat
import java.util.Date

import com.google.common.base.Strings

object DefaultValueResolver {

  val tags: Map[Field[_], FieldDefaultTag[_]] = Map(Assignee -> GUserTypeTag,
    DueDate -> DateTypeTag,
    CreatedOn -> DateTypeTag,
    UpdatedOn -> DateTypeTag,
    ClosedOn -> DateTypeTag,
    Reporter -> GUserTypeTag,
    EstimatedTime -> FloatTypeTag,
    Components -> SeqStringTypeTag)

  def getTag(field: Field[_]): FieldDefaultTag[_] = {
    tags.getOrElse(field, StringTypeTag)
  }
}

abstract sealed class FieldDefaultTag[T] {
  def parseDefault(str: String): T
}

object GUserTypeTag extends FieldDefaultTag[GUser] {
  override def parseDefault(str: String): GUser =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[GUser] else GUser(null, str, null)
}

object DateTypeTag extends FieldDefaultTag[Date] {
  /**
    * Format for dates in "default value if empty " fields on "Task Fields Mapping" panel.
    */
  val DATE_PARSER = new SimpleDateFormat("yyyy MM dd")

  override def parseDefault(str: String): Date =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[Date] else DATE_PARSER.parse(str)
}

object SeqStringTypeTag extends FieldDefaultTag[Seq[String]] {
  override def parseDefault(str: String): Seq[String] = str.split(' ')
}

object StringTypeTag extends FieldDefaultTag[String] {
  override def parseDefault(str: String): String = str
}

object FloatTypeTag extends FieldDefaultTag[Float] {
  override def parseDefault(str: String): Float =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[Float] else str.toFloat
}

object IntegerTypeTag extends FieldDefaultTag[Integer] {
  override def parseDefault(str: String): Integer =
    if (Strings.isNullOrEmpty(str))null.asInstanceOf[Integer] else str.toInt
}