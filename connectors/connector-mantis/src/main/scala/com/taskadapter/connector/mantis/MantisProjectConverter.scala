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

  def toGProject(mantisProject: ProjectData) = new GProject()
    .setId(mantisProject.getId.longValue())
    .setName(mantisProject.getName)
    .setKey(String.valueOf(mantisProject.getId))
    .setDescription(mantisProject.getDescription)
    .setHomepage("")
}
