package com.taskadapter.integrationtests

import java.util.Calendar

import com.taskadapter.connector.redmine.RedmineManagerFactory
import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.{Project, ProjectFactory}
import org.slf4j.LoggerFactory

object RedmineTestInitializer {
  private val logger = LoggerFactory.getLogger(RedmineTestInitializer.getClass)
  private val setup = TestConfigs.getRedmineServerInfo
  // TODO TA3 reuse the same http client everywhere instead of creating it here
  val httpClient = RedmineManagerFactory.createRedmineHttpClient

  var mgr: RedmineManager = RedmineManagerFactory.createRedmineManager(setup, httpClient)

  def createProject: Project = {
    logger.info("Running Redmine tests with: " + setup)
    val project = ProjectFactory.create("integration tests", "itest" + Calendar.getInstance.getTimeInMillis)
    try {
      val redmineProject = mgr.getProjectManager.createProject(project)
      logger.info("Created temporary Redmine project with key " + redmineProject.getIdentifier)
      redmineProject
    } catch {
      case e: Exception =>
        throw new RuntimeException(e)
    }
  }

  def deleteProject(projectKey: String): Unit = {
    try {
      mgr.getProjectManager.deleteProject(projectKey)
      logger.info("Deleted temporary Redmine project with ID " + projectKey)
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
