package com.taskadapter.config

import com.taskadapter.web.uiapi.SetupId

/**
  * Stored connector configuration. Just a plain data and nothing else.
  */
case class StoredConnectorConfig(connectorTypeId: String, connectorSavedSetupId: SetupId, serializedConfig: String)