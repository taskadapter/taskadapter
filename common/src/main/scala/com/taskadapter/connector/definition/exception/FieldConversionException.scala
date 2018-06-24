package com.taskadapter.connector.definition.exception

import com.taskadapter.connector.common.FieldPrettyNameBuilder
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.Field

case class FieldConversionException(connectorId: String, field: Field[_], value: Any, details: String) extends ConnectorException {

  override def getMessage: String = {
    val valueString = value match {
      case v: Seq[_] =>
        val seq = v.asInstanceOf[Seq[_]]
        if (seq.isEmpty) {
          "Empty collection"
        } else {
          "Collection of (" + v.mkString(",") + ")"
        }
      case _ => s"Value '$value'"
    }
    s"$valueString cannot be saved in field '" + FieldPrettyNameBuilder.getPrettyFieldName(field) +
      s"' by $connectorId connector. Reason: $details. Please verify that the field mapping for this field makes sense. " +
      s"Type checks will be improved in future app versions"
  }
}
