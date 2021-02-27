package com.taskadapter.connector.msp

import com.taskadapter.connector.common.TreeUtils
import com.taskadapter.connector.definition.FileSetup
import com.taskadapter.connector.testlib.{CommonTestChecks, FieldRowBuilder, ITFixture, TempFolder, TestUtilsJava}
import com.taskadapter.model._
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import java.io.File
import java.text.SimpleDateFormat
import java.util
import java.util.Date
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class MspConnectorTest extends FunSpec with Matchers with TempFolder {

  val MSP_FILE_NAME = "msp_test_data.xml"

  it("connector ID is not changed") {
    // make sure Connector ID is not changes accidentally. it must stay the same to support loading old configs
    // even if Connector "label" is changed.
    MSPConnector.ID shouldBe "Microsoft Project"
  }

  it("task is created and loaded") {
    withTempFolder { folder =>
      val fixture = ITFixture("ta-test.tmp", getConnector(folder), CommonTestChecks.skipCleanup)
      val task = new GTaskBuilder().withRandom(Summary)
        .withRandom(MspField.taskDuration)
        .withRandom(MspField.taskWork)
        .withRandom(MspField.mustStartOn)
        .withRandom(MspField.finish)
        .withRandom(MspField.deadline)
        .build()
        .setValue(Description, "desc")
        .setValue(AssigneeFullName, "display name")
        .setValue(Priority, java.lang.Integer.valueOf(888))
      fixture.taskIsCreatedAndLoaded(task,
        Seq(AssigneeFullName, MspField.taskDuration, MspField.taskWork, MspField.mustStartOn, MspField.finish, MspField.deadline,
          Description, Priority, Summary))
    }
  }

  it("description saved by default") {
    withTempFolder { folder =>
      CommonTestChecks.fieldIsSavedByDefault(getConnector(folder),
        new GTaskBuilder().withRandom(Summary).withRandom(Description).build(),
        MspField.fields,
        Description,
        CommonTestChecks.skipCleanup)
    }
  }

  it("trims field value and removes line break at the end") {
    withTempFolder { folder =>
      val textWithEndingLineBreak = " text " + System.lineSeparator()
      val task = new GTask().setValue(Summary, textWithEndingLineBreak)
      val created = TestUtilsJava.saveAndLoad(getConnector(folder), task, FieldRowBuilder.rows(Seq(Summary)).asJava)
      created.getValue(Summary) shouldBe "text"
    }
  }

  it("twoTasksAreCreated") {
    withTempFolder { folder =>
      CommonTestChecks.createsTasks(getConnector(folder), MspFieldBuilder.getDefault(),
        List(GTaskBuilder.withRandom(Summary), GTaskBuilder.withRandom(Summary)),
        CommonTestChecks.skipCleanup
      )
    }
  }

  it("tasks are loaded as tree") {
    val loadedTasks: util.List[GTask] = MSPTestUtils.load("Projeto1.xml")
    assertEquals(3, loadedTasks.size)
    val tree: util.List[GTask] = TreeUtils.buildTreeFromFlatList(loadedTasks)
    assertEquals(1, tree.size)
    assertEquals(2, tree.get(0).getChildren.size)
  }

  it("file created by MSP 2013 is loaded") {
    val tasks: util.List[GTask] = MSPTestUtils.load("msp_2013.xml")
    assertEquals(2, tasks.size)
    val task1: GTask = tasks.get(0)
    assertEquals("task 1", task1.getValue(Summary))
    task1.getValue(AssigneeFullName) shouldBe "alex"
    task1.getValue(MspField.taskDuration) shouldBe 12f
    val expectedStartDate: Date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/11/2013 08:00")
    assertEquals(expectedStartDate, task1.getValue(MspField.startAsSoonAsPossible))
    val expectedFinishDate: Date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/12/2013 12:00")
    assertEquals(expectedFinishDate, task1.getValue(MspField.finish))
  }

  def getConnector(folder: File): MSPConnector = {
    val file = new File(folder, MSP_FILE_NAME)
    val setup = FileSetup.apply(MSPConnector.ID, "label", file.getAbsolutePath, file.getAbsolutePath)
    new MSPConnector(setup)
  }
}
