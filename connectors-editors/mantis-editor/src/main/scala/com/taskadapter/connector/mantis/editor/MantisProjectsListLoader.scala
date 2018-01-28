package com.taskadapter.connector.mantis.editor

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException
import com.taskadapter.connector.mantis.{MantisManager, MantisManagerFactory, MantisProjectConverter}
import com.taskadapter.model.{GProject, NamedKeyedObject}
import com.taskadapter.web.callbacks.DataProvider

class MantisProjectsListLoader(setup: WebConnectorSetup) extends DataProvider[java.util.List[_ <: NamedKeyedObject]] {
  @throws[ServerURLNotSetException]
  override def loadData(): java.util.List[GProject] = {
    validate(setup)
    val mgr: MantisManager = MantisManagerFactory.createMantisManager(setup)
    try {
      val mntProjects = mgr.getProjects
      new MantisProjectConverter().toGProjects(mntProjects)
    } catch {
      case e: Exception =>
        throw new RuntimeException(e.toString, e)
    }
  }

  @throws[ServerURLNotSetException]
  private def validate(setup: WebConnectorSetup): Unit = {
    if (Strings.isNullOrEmpty(setup.host)) throw new ServerURLNotSetException
  }
}
