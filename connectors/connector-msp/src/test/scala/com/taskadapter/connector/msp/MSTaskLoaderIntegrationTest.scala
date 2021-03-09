package com.taskadapter.connector.msp

import com.taskadapter.connector.testlib.TestUtils
import com.taskadapter.model.{AllFields, CustomString, Summary}
import net.sf.mpxj.TaskField
import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class MSTaskLoaderIntegrationTest extends FunSpec with Matchers {
  it("testLoadFileCreatedByMSPWith1Task") {
    val tasks = MSPTestUtils.load("created_by_msp_1task.xml")
    Assert.assertEquals(1, tasks.size)
  }

  it("testFind1Task") {
    val tasks = MSPTestUtils.load("created_by_msp_1task.xml")
    val myTaskAddedFromMSP = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "task1")
    if (myTaskAddedFromMSP == null) Assert.fail("required task not found in the tasks list")
  }

  it("testLoadFileCreatedByMSPWithManyTasks") {
    val tasks = MSPTestUtils.load("created_by_msp_tasks.xml")
    Assert.assertEquals(4, tasks.size)
    val t1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "task1")
    Assert.assertNotNull("required task not found in the tasks list", t1)
    val t1Sub1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "task1-sub1")
    Assert.assertNotNull("required task not found in the tasks list", t1Sub1)
    val t2 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "task2")
    Assert.assertNotNull("required task not found in the tasks list", t2)
  }

  it("load file created by TA") {
    val tasks = MSPTestUtils.load("created_by_ta_27.xml")
    Assert.assertEquals(27, tasks.size)
    val t1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "improve components")
    Assert.assertNotNull("required task not found in the tasks list", t1)

    // verify TextXX fields are loaded. this is a regression test for
    // https://bitbucket.org/taskadapter/taskadapter/issues/74/issue-type-is-reset-to-default-setting
    t1.getValue(new CustomString(TaskField.TEXT1.getName)) shouldBe "49"
    t1.getValue(new CustomString(TaskField.TEXT2.getName)) shouldBe "Feature"

    val sub1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "sub1")
    Assert.assertNotNull("required task not found in the tasks list", sub1)
  }

  it("load file created by TA 1 task") {
    val tasks = MSPTestUtils.load("created_by_ta_1.xml")
    Assert.assertEquals(1, tasks.size)
    val t1 = TestUtils.findTaskByFieldName(tasks, AllFields.summary, "support me!")
    Assert.assertNotNull("required task not found in the tasks list", t1)
  }

  it("empty lines in MSP XML file are skipped") {
    // total number of lines is 170 with 163 non-empty ones
    val tasks = MSPTestUtils.load("IT_Department_Project_Master.xml").asScala
    Assert.assertEquals(163, tasks.size)
  }
}
