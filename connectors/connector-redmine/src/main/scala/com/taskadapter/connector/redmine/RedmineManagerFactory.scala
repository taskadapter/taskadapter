package com.taskadapter.connector.redmine

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.redmineapi.RedmineManager
import org.apache.http.client.HttpClient

object RedmineManagerFactory {
  def createRedmineHttpClient(): HttpClient = {
    try {
      val insecureConnectionManager = com.taskadapter.redmineapi.RedmineManagerFactory.createInsecureConnectionManager
      com.taskadapter.redmineapi.RedmineManagerFactory.getNewHttpClient(insecureConnectionManager)
    } catch {
      case e: Exception =>
        throw new RuntimeException("cannot create a connection manager for insecure SSL connections", e)
    }
  }

  def createRedmineManager(setup: WebConnectorSetup, client: HttpClient): RedmineManager = {
    if (setup.useApiKey) com.taskadapter.redmineapi.RedmineManagerFactory.createWithApiKey(setup.host, setup.apiKey, client)
    else com.taskadapter.redmineapi.RedmineManagerFactory.createWithUserAuth(setup.host, setup.userName, setup.password, client)
  }
}