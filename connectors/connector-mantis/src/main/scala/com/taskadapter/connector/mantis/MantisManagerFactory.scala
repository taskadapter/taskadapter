package com.taskadapter.connector.mantis

import com.taskadapter.connector.definition.WebConnectorSetup

object MantisManagerFactory {
  def createMantisManager(setup: WebConnectorSetup): MantisManager = {
    if (setup.useApiKey) {
      throw new RuntimeException("authorization using API key is not supported for Mantis Connector")
    } else {
      new MantisManager(setup.host, setup.userName, setup.password)
    }
  }
}
