package com.taskadapter.connector.github.editor

import java.util

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{ConnectorException, ServerURLNotSetException}
import com.taskadapter.connector.github.{ConnectionFactory, GithubProjectConverter}
import com.taskadapter.model.NamedKeyedObject
import com.taskadapter.web.callbacks.DataProvider
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.service.RepositoryService

class GithubProjectsListLoader(setup: WebConnectorSetup) extends DataProvider[java.util.List[_ <: NamedKeyedObject]] {
  @throws[ConnectorException]
  override def loadData(): java.util.List[_ <: NamedKeyedObject] = {
    validateServer(setup)
    try {
      val connectionFactory: ConnectionFactory = new ConnectionFactory(setup)
      val repositoryService: RepositoryService = connectionFactory.getRepositoryService
      val repositories: util.List[Repository] = repositoryService.getRepositories(setup.userName)
      val converter: GithubProjectConverter = new GithubProjectConverter
      converter.toGProjects(repositories)
    } catch {
      case e: Exception =>
        throw new RuntimeException(e.toString, e)
    }
  }

  @throws[ServerURLNotSetException]
  private def validateServer(setup: WebConnectorSetup): Unit = {
    if (Strings.isNullOrEmpty(setup.host)) throw new ServerURLNotSetException
  }
}
