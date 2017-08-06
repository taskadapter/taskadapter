package com.taskadapter.config

/**
  * Stored connector configuration. Just a plain data and nothing else.
  */
case class StoredConnectorConfig(connectorTypeId: String, connectorSavedSetupId: String, serializedConfig: String)