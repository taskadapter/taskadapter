package com.taskadapter.connector.definition

sealed trait ConnectorSetup {
  def label: String
}

/**
  * @param connectorId is used to find all existing setups for, say, JIRA to show on "new config" page.
  */
case class WebConnectorSetup(connectorId: String,
                             label: String,
                             host: String,
                             userName: String,
                             password: String,
                             useApiKey: Boolean,
                             apiKey: String) extends ConnectorSetup

case class FileSetup(connectorId: String, label: String, sourceFile: String, targetFile: String) extends ConnectorSetup
