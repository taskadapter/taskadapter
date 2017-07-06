package com.taskadapter.connector

import com.taskadapter.connector.it.{RedmineTestConfig, RedmineTestInitializer}
import com.taskadapter.connector.redmine.RedmineConnector
import com.taskadapter.redmineapi.bean.Project
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class NewIt extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private var redmineProject: Option[Project] = None

  private val mgr = RedmineTestInitializer.mgr
  val sourceConfig = RedmineTestConfig.getRedmineTestConfig
  val targetConfig = RedmineTestConfig.getRedmineTestConfig

  override def beforeAll = {
    redmineProject = Some(RedmineTestInitializer.createProject)
//    sourceConfig.setProjectKey(redmineProject.get.getIdentifier)
    // XXX query id is hardcoded
//    sourceConfig.setQueryId(1)

    targetConfig.setProjectKey(redmineProject.get.getIdentifier)

  }

  override def afterAll(): Unit = {
    RedmineTestInitializer.deleteProject(redmineProject.get.getIdentifier)
  }

  it("loads and saves") {
    val sourceConnector = new RedmineConnector(sourceConfig)
    val targetConnector = new RedmineConnector(targetConfig)

    val adapter = new Adapter(sourceConnector, targetConnector)
    adapter.adapt()

  }

}
