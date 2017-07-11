package com.taskadapter.connector.common

import java.text.{ParseException, SimpleDateFormat}
import java.util.Date

import com.google.common.base.Strings
import com.taskadapter.connector.{Field, FieldRow}
import com.taskadapter.model.GTask

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

  def adapt(fieldRows: java.lang.Iterable[FieldRow], task: GTask): GTask = {
    val result = new GTask
    fieldRows.asScala.foreach { row =>
      val fieldToLoadValueFrom = row.sourceField
      val currentFieldValue = task.getValue(fieldToLoadValueFrom.name)
      var newValue = currentFieldValue
      if (fieldIsConsideredEmpty(currentFieldValue)) {
        val valueWithProperType = getValueWithProperType(fieldToLoadValueFrom, row.defaultValueForEmpty)
        newValue = valueWithProperType
      }
      val targetFieldName = row.targetField.name
      if (targetFieldName == null || targetFieldName == "") {
        throw new RuntimeException(s"Target field name is null. These fields should have been filtered before calling this method. row: $row")
      }
      result.setValue(targetFieldName, newValue)
    }
    result.setId(task.getId)
    result.setParentKey(task.getParentKey)
    result
  }

  private def fieldIsConsideredEmpty(value: Any) = (value == null) || (value.isInstanceOf[String] && value.asInstanceOf[String].isEmpty)

  private def getValueWithProperType(field: Field, value: String): Object = {

    import scala.language.implicitConversions
    field.typeName match {
      case "Date" => parseDate(value)
      case "Float" => parseFloat(value).asInstanceOf[Object]
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

