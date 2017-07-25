package com.taskadapter.config

/**
  * @param connectorId is used to find all existing setups for, say, JIRA to show on "new config" page.
  */
case class ConnectorSetup(connectorId: String,
                          label: String,
                          host: String,
                          userName: String,
                          password: String,
                          useApiKey: Boolean,
                          apiKey: String)
