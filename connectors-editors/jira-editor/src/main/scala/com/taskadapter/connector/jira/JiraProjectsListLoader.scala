package com.taskadapter.connector.jira

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.{GProject, NamedKeyedObject}
import com.taskadapter.web.callbacks.DataProvider

class JiraProjectsListLoader(setup: WebConnectorSetup) extends DataProvider[java.util.List[_ <: NamedKeyedObject]] {
  @throws[ConnectorException]
  override def loadData(): java.util.List[GProject] = {
    JiraLoaders.validate(setup)
    try {
      val client = JiraConnectionFactory.createClient(setup)
      try {
        val promise = client.getProjectClient.getAllProjects
        val projects = promise.claim
        val gProjects = new JiraProjectConverter().toGProjects(projects)
        gProjects
      } catch {
        case e: Exception =>
          throw JiraUtils.convertException(e)
      } finally if (client != null) client.close()
    }
  }
}
