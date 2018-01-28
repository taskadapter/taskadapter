package com.taskadapter.connector.jira

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{ConnectorException, ProjectNotSetException, ServerURLNotSetException}
import com.taskadapter.model.GProject
import com.taskadapter.web.callbacks.DataProvider

class JiraProjectLoader(jiraConfig: JiraConfig, setup: WebConnectorSetup) extends DataProvider[GProject] {
  /**
    * Load project info.
    */
  @throws[ConnectorException]
  override def loadData: GProject = {
    if (Strings.isNullOrEmpty(setup.host)) throw new ServerURLNotSetException
    if (jiraConfig.getProjectKey == null || jiraConfig.getProjectKey.isEmpty) throw new ProjectNotSetException
    val project = loadProject(setup, jiraConfig.getProjectKey)
    project
  }

  @throws[ConnectorException]
  private def loadProject(setup: WebConnectorSetup, projectKey: String): GProject = {
    JiraLoaders.validate(setup)
    try {
      val client = JiraConnectionFactory.createClient(setup)
      try {
        val promise = client.getProjectClient.getProject(projectKey)
        val project = promise.claim
        val gProject = new JiraProjectConverter().toGProject(project)
        gProject
      } catch {
        case e: Exception =>
          throw JiraUtils.convertException(e)
      } finally if (client != null) client.close()
    }
  }
}
