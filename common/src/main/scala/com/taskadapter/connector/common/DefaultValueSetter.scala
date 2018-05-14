package com.taskadapter.connector.common

import java.text.SimpleDateFormat

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

  def adapt(fieldRows: Iterable[FieldRow[_]], task: GTask): GTask = {
    val result = new GTask
    fieldRows.foreach { row => adaptRow(task, result, row) }
    result.setSourceSystemId(task.getSourceSystemId)
    result.setParentIdentity(task.getParentIdentity)
    result.setChildren(task.getChildren)
    result
  }

  private def adaptRow[T](task: GTask, result: GTask, row: FieldRow[T]): Unit = {
    val fieldToLoadValueFrom = row.sourceField
    val currentFieldValue = fieldToLoadValueFrom.map(f => task.getValue(f)).flatMap(e => Option(e))

    val newValue = if (fieldIsConsideredEmpty(currentFieldValue)) {
      row.defaultValueForEmpty
    } else {
      currentFieldValue.get
    }
    val targetField = row.targetField.get
    result.setValue(targetField, newValue)
  }

  private def fieldIsConsideredEmpty(value: Option[Any]) =
    value.isEmpty || value.get.isInstanceOf[String] && value.get.asInstanceOf[String].isEmpty
}

