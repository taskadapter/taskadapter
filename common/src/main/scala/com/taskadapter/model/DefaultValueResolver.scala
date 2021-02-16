package com.taskadapter.model

import java.text.SimpleDateFormat
import java.util.Date

import com.google.common.base.Strings

object DefaultValueResolver {

  val tags: Map[Field[_], FieldDefaultTag[_]] = Map(
    AssigneeLoginName -> StringTypeTag,
    AssigneeFullName -> StringTypeTag,
    ClosedOn -> DateTypeTag,
    Components -> SeqStringTypeTag,
    CreatedOn -> DateTypeTag,
    DueDate -> DateTypeTag,
    DoneRatio -> FloatTypeTag,
    Description -> StringTypeTag,
    EstimatedTime -> FloatTypeTag,
//    SpentTime -> FloatTypeTag,
    Id -> LongTypeTag,
    Priority -> IntegerTypeTag,
    ReporterFullName -> StringTypeTag,
    ReporterLoginName -> StringTypeTag,
    StartDate -> DateTypeTag,
    UpdatedOn -> DateTypeTag,
  )

  def getTag(field: Field[_]): FieldDefaultTag[_] = {
    field match {
      case _: CustomDate => DateTypeTag
      case _: CustomFloat=> FloatTypeTag
      case _: CustomSeqString=> SeqStringTypeTag
      case _: CustomString=> StringTypeTag
      case _ => tags.getOrElse(field, StringTypeTag)
    }
  }
}

abstract sealed class FieldDefaultTag[T] {
  def parseDefault(str: String): T
}

object GUserTypeTag extends FieldDefaultTag[GUser] {
  override def parseDefault(str: String): GUser =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[GUser] else new GUser().setLoginName(str)
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
  override def parseDefault(str: String): Seq[String] =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[Seq[String]] else str.split(' ')
}

object StringTypeTag extends FieldDefaultTag[String] {
  override def parseDefault(str: String): String = str
}

object FloatTypeTag extends FieldDefaultTag[Float] {
  override def parseDefault(str: String): Float =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[Float] else str.toFloat
}

object LongTypeTag extends FieldDefaultTag[java.lang.Long] {
  override def parseDefault(str: String): java.lang.Long =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[java.lang.Long] else str.toLong
}

object IntegerTypeTag extends FieldDefaultTag[Integer] {
  override def parseDefault(str: String): Integer =
    if (Strings.isNullOrEmpty(str)) null.asInstanceOf[Integer] else str.toInt
}