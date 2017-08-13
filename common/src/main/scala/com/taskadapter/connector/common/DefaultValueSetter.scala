package com.taskadapter.connector.common

import java.text.{ParseException, SimpleDateFormat}
import java.util.Date

import com.google.common.base.Strings
import com.taskadapter.connector.FieldRow
import com.taskadapter.model.GTask

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

  def adapt(fieldRows: Iterable[FieldRow], task: GTask): GTask = {
    val result = new GTask
    fieldRows.foreach { row =>
      val fieldToLoadValueFrom = row.sourceField
      val currentFieldValue = fieldToLoadValueFrom.map(task.getValue).flatMap(e => Option(e))

      val newValue = if (fieldIsConsideredEmpty(currentFieldValue)) {
        val valueWithProperType = getValueWithProperType(
          fieldToLoadValueFrom.map(_.typeName).getOrElse("String"),
          row.defaultValueForEmpty)
        valueWithProperType
      } else {
        currentFieldValue.get
      }
      if (row.targetField.isEmpty || row.targetField.get.name == null || row.targetField.get.name == "") {
        throw new RuntimeException(s"Target field name is null. These fields should have been filtered out before calling this method. row: $row")
      }
      val targetFieldName = row.targetField.get.name
      result.setValue(targetFieldName, newValue)
    }
    result.setSourceSystemId(task.getSourceSystemId)
    result.setParentIdentity(task.getParentIdentity)
    result
  }

  private def fieldIsConsideredEmpty(value: Option[Any]) =
    value.isEmpty || value.get.isInstanceOf[String] && value.get.asInstanceOf[String].isEmpty

  private def getValueWithProperType(fieldTypeName: String, value: String): Object = {

    import scala.language.implicitConversions
    fieldTypeName match {
      case "Date" => parseDate(value)
      case "Float" => parseFloat(value).asInstanceOf[Object]
      case "Integer" => parseInteger(value).asInstanceOf[Object]
      case _ => value
    }
  }

  private def parseFloat(value: String): Float = {
    if (Strings.isNullOrEmpty(value))
      null.asInstanceOf[Float]
    else
      value.toFloat
  }
  private def parseInteger(value: String): Integer = {
    if (Strings.isNullOrEmpty(value))
      null.asInstanceOf[Integer]
    else
      value.toInt
  }

  @throws[ParseException]
  private def parseDate(value: String): Date = {
    if (Strings.isNullOrEmpty(value))
      null.asInstanceOf[Date]
    else DATE_PARSER.parse(value)
  }
}

