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
    val project = JiraLoaders.loadProject(setup, jiraConfig.getProjectKey)
    project
  }
}
