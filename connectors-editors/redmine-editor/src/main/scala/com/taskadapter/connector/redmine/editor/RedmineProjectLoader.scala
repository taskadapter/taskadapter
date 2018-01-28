package com.taskadapter.connector.redmine.editor

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{ConnectorException, ProjectNotSetException, ServerURLNotSetException}
import com.taskadapter.connector.redmine.converter.RedmineProjectConverter
import com.taskadapter.connector.redmine.{RedmineConfig, RedmineManagerFactory}
import com.taskadapter.model.GProject
import com.taskadapter.web.callbacks.DataProvider

class RedmineProjectLoader(config: RedmineConfig, setup: WebConnectorSetup) extends DataProvider[GProject] {
  private val httpClient = RedmineManagerFactory.createRedmineHttpClient

  /**
    * Load project info.
    */
  @throws[ConnectorException]
  override def loadData: GProject = {
    if (Strings.isNullOrEmpty(setup.host)) throw new ServerURLNotSetException
    if (config.getProjectKey == null || config.getProjectKey.isEmpty) throw new ProjectNotSetException
    val redmineManager = RedmineManagerFactory.createRedmineManager(setup, httpClient)
    val project = RedmineLoaders.loadProject(redmineManager.getProjectManager, config.getProjectKey)
    RedmineProjectConverter.convertToGProject(project)
  }
}
