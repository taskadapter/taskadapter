package com.taskadapter.connector.common

import java.text.{ParseException, SimpleDateFormat}
import java.util
import java.util.Date

import com.google.common.base.Strings
import com.taskadapter.connector.FieldRow
import com.taskadapter.model.{GTask, GTaskDescriptor}

import scala.collection.JavaConverters._

/**
  * When saving a task, we need to set some of its fields to some default value if there is nothing there yet.
  * E.g. "environment" field can be set as required in Jira and then saving data to Jira will fail
  * if the source data does not contain that info. To fix this, we have "default value if empty" column on
  * "Task Fields Mapping" panel.
  * <p>
  * This class sets those default values to empty fields.
  */
object DefaultValueSetter {
  /**
    * Format for dates in "default value if empty " fields on "Task Fields Mapping" panel.
    */
  private val DATE_PARSER = new SimpleDateFormat("yyyy MM dd")

  def adapt(fieldRows: util.List[FieldRow], task: GTask): GTask = {
    val result = new GTask
    fieldRows.asScala.foreach { row =>
      val fieldToLoadValueFrom = row.nameInSource
      val currentFieldValue = task.getValue(fieldToLoadValueFrom)
      var newValue = currentFieldValue
      if (fieldIsConsideredEmpty(currentFieldValue)) {
        val valueWithProperType = getValueWithProperType(fieldToLoadValueFrom, row.defaultValueForEmpty)
        newValue = valueWithProperType
      }
      result.setValue(row.nameInTarget, newValue)
    }
    result.setId(task.getId)
    result.setParentKey(task.getParentKey)
    result
  }

  private def fieldIsConsideredEmpty(value: Any) = (value == null) || (value.isInstanceOf[String] && value.asInstanceOf[String].isEmpty)

  private def getValueWithProperType(field: String, value: String): Object = try {
    val enumElement = GTaskDescriptor.FIELD.valueOf(field.toUpperCase)
    getValueWithProperType(enumElement, value)
  } catch {
    case e: Exception =>
      value // string by default
  }

  private def getValueWithProperType(field: GTaskDescriptor.FIELD, value: String): Object = {
    // TODO REVIEW Should this code be polymorphic and belong to a GTaskDespciptor.FIELD instances?
    // It would be more extensible. Same for fieldIsConsideredEmpty.
    import scala.collection.JavaConversions._
    import scala.language.implicitConversions

    field match {
      case GTaskDescriptor.FIELD.START_DATE =>
        parseDate(value)
      case GTaskDescriptor.FIELD.DUE_DATE =>
        parseDate(value)
      case GTaskDescriptor.FIELD.ESTIMATED_TIME =>
        parseFloat(value).asInstanceOf[Object]
      case GTaskDescriptor.FIELD.DONE_RATIO =>
        parseFloat(value).asInstanceOf[Object]
      case GTaskDescriptor.FIELD.PRIORITY =>
        parseFloat(value).asInstanceOf[Object]
      case _ => value
    }
  }

  private def parseFloat(value: String): Float = {
    if (Strings.isNullOrEmpty(value))
      null.asInstanceOf[Float]
    else
      value.toFloat
  }

  @throws[ParseException]
  private def parseDate(value: String): Date = {
    if (Strings.isNullOrEmpty(value))
      null.asInstanceOf[Date]
    else DATE_PARSER.parse(value)
  }
}

