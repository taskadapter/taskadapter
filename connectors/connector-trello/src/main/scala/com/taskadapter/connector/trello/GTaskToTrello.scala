package com.taskadapter.connector.trello

import java.util.Date

import com.julienvey.trello.domain.{Card, TList}
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.exception.FieldConversionException
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.{Children, Description, DueDate, GTask, Id, Key, ParentKey, Relations, SourceSystemId, Summary}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class GTaskToTrello(config:TrelloConfig, listCache: ListCache) extends ConnectorConverter[GTask, Card] {
  val logger = LoggerFactory.getLogger(classOf[GTaskToTrello])

  override def convert(source: GTask): Card = {
    val card = new Card
    card.setId(source.getKey)
    card.setIdBoard(config.getBoardId)
    source.getFields.asScala.foreach { e =>
      val field = e._1
      val value = e._2
      try {
        field match {
          case Children => // processed in another place
          case Id => // ignore ID field because it does not need to be provided when saving
          case Key => // processed in [[DefaultValueSetter]]
          case SourceSystemId => // processed in [[DefaultValueSetter]]
          case ParentKey => // processed above
          case Relations => // processed in another place

          case TrelloField.listId =>
            card.setIdList(value.asInstanceOf[String])
          case TrelloField.listName =>
            val listName = value.asInstanceOf[String]
            val listId = listCache.getListIdByName(listName)
            if (listId.isDefined) {
              card.setIdList(listId.get)
            } else {
              throw new ConnectorException(
                s"Trello list with name '$listName' is not found on the requested Trello Board (board ID ${config.getBoardId} )")
            }
          case Summary => card.setName(value.asInstanceOf[String])
          case Description => card.setDesc(value.asInstanceOf[String])
          case DueDate => card.setDue(value.asInstanceOf[Date])
          case _ => logger.warn(s"Unknown field in GTask: $field. Skipping it")
        }
      } catch {
        case e: ConnectorException => throw e
        case e: Exception => throw new FieldConversionException(TrelloConnector.ID, field, value, e.getMessage)
      }
    }
    card
  }
}
