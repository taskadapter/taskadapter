package com.taskadapter.connector.msp

import com.taskadapter.connector.msp.MSPTestUtils.loadWithDefaultMappings
import com.taskadapter.connector.testlib.TestUtils
import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class MSTaskLoaderIntegrationTest extends FunSpec with Matchers {
  it("testLoadFileCreatedByMSPWith1Task") {
    val tasks = loadWithDefaultMappings("created_by_msp_1task.xml")
    Assert.assertEquals(1, tasks.size)
  }

  it("testFind1Task") {
    val tasks = loadWithDefaultMappings("created_by_msp_1task.xml").asScala
    val myTaskAddedFromMSP = TestUtils.findTaskByFieldName(tasks, MspField.summary.name, "task1")
    if (myTaskAddedFromMSP == null) Assert.fail("required task not found in the tasks list")
  }

  it("testLoadFileCreatedByMSPWithManyTasks") {
    val tasks = loadWithDefaultMappings("created_by_msp_tasks.xml").asScala
    Assert.assertEquals(4, tasks.size)
    val t1 = TestUtils.findTaskByFieldName(tasks, MspField.summary.name, "task1")
    Assert.assertNotNull("required task not found in the tasks list", t1)
    val t1Sub1 = TestUtils.findTaskByFieldName(tasks, MspField.summary.name, "task1-sub1")
    Assert.assertNotNull("required task not found in the tasks list", t1Sub1)
    val t2 = TestUtils.findTaskByFieldName(tasks, MspField.summary.name, "task2")
    Assert.assertNotNull("required task not found in the tasks list", t2)
  }

  it("testLoadFileCreatedByTA") {
    val tasks = loadWithDefaultMappings("created_by_ta_27.xml").asScala
    Assert.assertEquals(27, tasks.size)
    val t1 = TestUtils.findTaskByFieldName(tasks, MspField.summary.name, "improve components")
    Assert.assertNotNull("required task not found in the tasks list", t1)
    val sub1 = TestUtils.findTaskByFieldName(tasks, MspField.summary.name, "sub1")
    Assert.assertNotNull("required task not found in the tasks list", sub1)
  }

  it("testLoadFileCreatedByTA1Task") {
    val tasks = loadWithDefaultMappings("created_by_ta_1.xml").asScala
    Assert.assertEquals(1, tasks.size)
    val t1 = TestUtils.findTaskByFieldName(tasks, MspField.summary.name, "support me!")
    Assert.assertNotNull("required task not found in the tasks list", t1)
  }

  it("testEmptyLinesAreSkipped") { // total number of lines is 170 with 163 non-empty ones
    val tasks = loadWithDefaultMappings("IT_Department_Project_Master.xml").asScala
    Assert.assertEquals(163, tasks.size)
  }
}