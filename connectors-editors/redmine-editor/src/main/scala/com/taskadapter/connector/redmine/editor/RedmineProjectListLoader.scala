package com.taskadapter.connector.redmine.editor

import java.util

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException
import com.taskadapter.connector.redmine.RedmineManagerFactory
import com.taskadapter.connector.redmine.converter.RedmineProjectConverter
import com.taskadapter.model.NamedKeyedObject
import com.taskadapter.redmineapi.RedmineException
import com.taskadapter.web.callbacks.DataProvider

class RedmineProjectListLoader(setup: WebConnectorSetup) extends DataProvider[java.util.List[_ <: NamedKeyedObject]] {
  private val httpClient = RedmineManagerFactory.createRedmineHttpClient(setup.host)
  val mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient)

  @throws[ServerURLNotSetException]
  override def loadData(): util.List[_ <: NamedKeyedObject] = {
      RedmineLoaders.validate(setup)
      try {
        val rmProjects = mgr.getProjectManager.getProjects
        new RedmineProjectConverter().toGProjects(rmProjects)
      } catch {
        case e: RedmineException =>
          throw new RuntimeException(e.toString, e)
      }
  }
}
