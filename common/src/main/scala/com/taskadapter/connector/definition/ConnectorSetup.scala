package com.taskadapter.connector.definition

import scala.beans.BeanProperty

sealed trait ConnectorSetup {
  def connectorId: String
  def label: String
  def id: Option[String]
}

object WebConnectorSetup {
  def apply(connectorId: String,
            label: String,
            host: String,
            userName: String,
            password: String,
            useApiKey: Boolean,
            apiKey: String): WebConnectorSetup = WebConnectorSetup(connectorId, None, label, host, userName, password, useApiKey, apiKey)
}

/**
  * @param connectorId is used to find all existing setups for, say, JIRA to show on "new config" page.
  */
case class WebConnectorSetup(@BeanProperty var connectorId: String,
                             @BeanProperty var id: Option[String],
                             @BeanProperty var label: String,
                             @BeanProperty var host: String,
                             @BeanProperty var userName: String,
                             @BeanProperty var password: String,
                             @BeanProperty var useApiKey: Boolean,
                             @BeanProperty var apiKey: String) extends ConnectorSetup

object FileSetup {
  def apply(connectorId: String,
            label: String,
            sourceFile: String,
            targetFile: String): FileSetup = new FileSetup(connectorId, None, label, sourceFile, targetFile)
}
case class FileSetup(connectorId: String,
                     id: Option[String],
                     label: String,
                     @BeanProperty var sourceFile: String,
                     @BeanProperty var targetFile: String) extends ConnectorSetup
