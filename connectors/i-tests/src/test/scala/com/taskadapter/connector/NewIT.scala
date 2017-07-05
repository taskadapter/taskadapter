package com.taskadapter.connector

import com.taskadapter.connector.it.{RedmineTestConfig, RedmineTestInitializer}
import com.taskadapter.connector.redmine.{RedmineConfig, RedmineConnector}
import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Project
import org.junit.runner.RunWith
import org.junit.runner.RunWith
import org.scalatest.concurrent.{ScalaFutures, Waiters}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class NewIt extends FunSpec with ScalaFutures with MockitoSugar with Matchers with BeforeAndAfter with BeforeAndAfterAll
  with Waiters {

  private var redmineProject: Option[Project] = None

  private val mgr = RedmineTestInitializer.mgr
  val sourceConfig = RedmineTestConfig.getRedmineTestConfig
  val targetConfig = RedmineTestConfig.getRedmineTestConfig

  override def beforeAll = {
    redmineProject = Some(RedmineTestInitializer.createProject)
    sourceConfig.setProjectKey(redmineProject.get.getIdentifier)
    // XXX query id is hardcoded
    sourceConfig.setQueryId(1)

    targetConfig.setProjectKey(redmineProject.get.getIdentifier)

  }

  override def afterAll(): Unit = {
    RedmineTestInitializer.deleteProject(redmineProject.get.getIdentifier)
  }

  describe("it") {
    it("loads") {
      val sourceConnector = new RedmineConnector(sourceConfig)
      val targetConnector = new RedmineConnector(targetConfig)

    }
  }

}
