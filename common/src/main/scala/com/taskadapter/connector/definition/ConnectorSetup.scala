package com.taskadapter.connector.definition

sealed trait ConnectorSetup {
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
case class WebConnectorSetup(connectorId: String,
                             id: Option[String],
                             label: String,
                             host: String,
                             userName: String,
                             password: String,
                             useApiKey: Boolean,
                             apiKey: String) extends ConnectorSetup

object FileSetup {
  def apply(connectorId: String,
            label: String,
            sourceFile: String,
            targetFile: String): FileSetup = new FileSetup(connectorId, None, label, sourceFile, targetFile)
}
case class FileSetup(connectorId: String,
                     id: Option[String],
                     label: String,
                     sourceFile: String,
                     targetFile: String) extends ConnectorSetup
