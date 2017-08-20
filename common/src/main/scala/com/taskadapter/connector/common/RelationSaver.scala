package com.taskadapter.connector.common

import java.util

import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.GRelation

trait RelationSaver {
  /**
    * Save task relations.
    *
    * @param relations relations to save.
    * @throws ConnectorException if connector fails to save a relation.
    */
  @throws[ConnectorException]
  def saveRelations(relations: util.List[GRelation]): Unit
}
