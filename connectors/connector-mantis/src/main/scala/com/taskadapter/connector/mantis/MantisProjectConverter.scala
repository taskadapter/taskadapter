package com.taskadapter.connector.mantis

import java.util

import biz.futureware.mantis.rpc.soap.client.ProjectData
import com.taskadapter.model.GProject

class MantisProjectConverter {
  def toGProjects(objects: util.List[ProjectData]): util.List[GProject] = {
    val projects = new util.ArrayList[GProject]
    import scala.collection.JavaConversions._
    for (rmProject <- objects) {
      val project = toGProject(rmProject)
      projects.add(project)
    }
    projects
  }

  def toGProject(mantisProject: ProjectData) = GProject(mantisProject.getId.intValue,
    mantisProject.getName,
    String.valueOf(mantisProject.getId), mantisProject.getDescription, "")
}
