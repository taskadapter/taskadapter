package com.taskadapter.connector.common.data

import com.taskadapter.connector.definition.exceptions.ConnectorException

/**
  * Converter between source and destination type.
  *
  * @tparam S source type.
  * @tparam T destination type.
  */
trait ConnectorConverter[S, T] {
  /**
    * Convert a task from source to target format.
    *
    * @param source source object to convert.
    * @return converted object
    */
  @throws[ConnectorException]
  def convert(source: S): T
}
