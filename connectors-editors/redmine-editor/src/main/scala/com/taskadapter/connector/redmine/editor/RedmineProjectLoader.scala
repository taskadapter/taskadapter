package com.taskadapter.connector.redmine.editor

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{ConnectorException, ProjectNotSetException, ServerURLNotSetException}
import com.taskadapter.connector.redmine.converter.RedmineProjectConverter
import com.taskadapter.connector.redmine.{RedmineConfig, RedmineManagerFactory}
import com.taskadapter.model.GProject
import com.taskadapter.redmineapi.bean.Project
import com.taskadapter.redmineapi.{ProjectManager, RedmineException}
import com.taskadapter.web.callbacks.DataProvider
import org.slf4j.{Logger, LoggerFactory}

class RedmineProjectLoader(config: RedmineConfig, setup: WebConnectorSetup) extends DataProvider[GProject] {
  private val logger = LoggerFactory.getLogger(classOf[RedmineProjectLoader])

  private val httpClient = RedmineManagerFactory.createRedmineHttpClient

  /**
    * Load project info.
    */
  @throws[ConnectorException]
  override def loadData: GProject = {
    if (Strings.isNullOrEmpty(setup.host)) throw new ServerURLNotSetException
    if (config.getProjectKey == null || config.getProjectKey.isEmpty) throw new ProjectNotSetException
    val redmineManager = RedmineManagerFactory.createRedmineManager(setup, httpClient)
    val project = loadProject(redmineManager.getProjectManager, config.getProjectKey)
    RedmineProjectConverter.convertToGProject(project)
  }

  /**
    * Loads a project.
    *
    * @param manager    manager.
    * @param projectKey project key.
    * @return loaded project.
    */
  private def loadProject(manager: ProjectManager, projectKey: String): Project = {
    try
      return manager.getProjectByKey(projectKey)
    catch {
      case e: RedmineException =>
        logger.error("Error loading redmine project with key '" + projectKey + "'. " + e.getMessage, e)
    }
    null
  }

}
