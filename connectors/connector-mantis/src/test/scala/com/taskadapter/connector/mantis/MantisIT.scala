package com.taskadapter.connector.mantis

import java.math.BigInteger
import java.util
import java.util.Calendar

import biz.futureware.mantis.rpc.soap.client.ProjectData
import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.testlib.{CommonTestChecks, TestSaver}
import com.taskadapter.model.GTaskBuilder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}
import org.slf4j.LoggerFactory

@RunWith(classOf[JUnitRunner])
class MantisIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  val logger = LoggerFactory.getLogger(classOf[MantisIT])
  val setup = MantisTestConfig.getSetup

  logger.info("Running Mantis BT tests using: " + setup.host)

  val mgr = new MantisManager(setup.host, setup.userName, setup.password)
  val junitTestProject = new ProjectData
  junitTestProject.setName("test project" + Calendar.getInstance.getTimeInMillis)
  junitTestProject.setDescription("test" + Calendar.getInstance.getTimeInMillis)
  val mantisUser = mgr.getCurrentUser
  val currentUser = MantisToGTask.convertToGUser(mantisUser)
  val projectId = mgr.createProject(junitTestProject)
  val projectKey = projectId.toString
  val config = new MantisConfig
  config.setProjectKey(projectKey)
  val mantisConnector = new MantisConnector(config, setup)

  override def afterAll() {
    if (mgr != null) mgr.deleteProject(new BigInteger(projectKey))
  }

/*
  it("assigneeExported") {
    val task = generateTask
    task.setAssignee(currentUser)
    val loadedTask = getTestSaver.selectField(FIELD.ASSIGNEE).saveAndLoad(task)
    assertEquals(currentUser.getId, loadedTask.getAssignee.getId)
  }

  it("assigneeExportedByDefault") {
    val task = generateTask
    task.setAssignee(currentUser)
    val loadedTask = getTestSaver.saveAndLoad(task)
    assertEquals(currentUser.getId, loadedTask.getAssignee.getId)
  }*/

  it("taskUpdatedOK") {
    val task = generateTask()
    CommonTestChecks.taskCreatedAndUpdatedOK(setup.host, mantisConnector, MantisFieldBuilder.getDefault,
      task, MantisField.summary, "new value",
      CommonTestChecks.skipCleanup)
  }

  def generateTask() = new GTaskBuilder().withRandom(MantisField.summary).build()

  private def getTestSaver(rows: List[FieldRow]) = new TestSaver(getConnector(), rows)

  private def getConnector(): MantisConnector = getConnector(config)

  private def getConnector(config: MantisConfig) = new MantisConnector(config, setup)
}
