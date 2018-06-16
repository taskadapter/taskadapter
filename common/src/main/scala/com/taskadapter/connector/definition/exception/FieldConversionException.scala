package com.taskadapter.connector.definition.exception

import com.taskadapter.connector.common.FieldPrettyNameBuilder
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.Field

case class FieldConversionException(connectorId: String, field: Field[_], value: Any) extends ConnectorException(
  s"Value $value cannot be saved in field '" + FieldPrettyNameBuilder.getPrettyFieldName(field) +
    s"' by $connectorId connector. Please verify that the source-target field mapping makes sense. " +
    s"Type checks will be improved in future app versions")
