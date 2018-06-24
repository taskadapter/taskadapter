package com.taskadapter.connector.trello

import java.util

import com.taskadapter.connector.Priorities
import com.taskadapter.connector.definition.ConnectorConfig

import scala.beans.BeanProperty

class TrelloConfig(@BeanProperty var boardId: String, @BeanProperty var boardName: String) extends ConnectorConfig {

  setPriorities(createDefaultPriorities)

  /**
    * Creates default priorities.
    *
    * @return default priorities.
    */
  def createDefaultPriorities: Priorities = {
    val result: util.Map[String, Integer] = new util.HashMap[String, Integer]
    result.put("Lowest", 100)
    result.put("Low", 300)
    result.put("Medium", 500)
    result.put("High", 700)
    result.put("Highest", 1000)
    new Priorities(result)
  }

}