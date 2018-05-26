package com.taskadapter.connector.msp

import java.io.File
import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.taskadapter.connector.common.TreeUtils
import com.taskadapter.connector.definition.FileSetup
import com.taskadapter.connector.testlib.{CommonTestChecks, FieldRowBuilder, TempFolder, TestSaver}
import com.taskadapter.model._
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

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
      CommonTestChecks.taskIsCreatedAndLoaded(getConnector(folder), GTaskBuilder.withRandom(Summary),
        MspFieldBuilder.getDefault(), Seq(Summary),
        CommonTestChecks.skipCleanup)
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


  it("twoTasksAreCreated") {
    withTempFolder { folder =>
      CommonTestChecks.createsTasks(getConnector(folder), MspFieldBuilder.getDefault(),
        List(GTaskBuilder.withRandom(Summary), GTaskBuilder.withRandom(Summary)),
        CommonTestChecks.skipCleanup
      )
    }
  }

  it("fields are saved") {
    withTempFolder { folder =>
      val task = new GTaskBuilder()
        .withRandom(Assignee)
        .withRandom(MspField.taskDuration)
        .withRandom(MspField.taskWork)
        .withRandom(MspField.mustStartOn)
        .withRandom(MspField.finish)
        .withRandom(MspField.deadline)
        .build()
      val loaded = new TestSaver(getConnector(folder),
        FieldRowBuilder.rows(Seq(
          Summary,
          Assignee,
          MspField.taskDuration,
          MspField.mustStartOn,
          MspField.taskWork,
          MspField.finish,
          MspField.deadline)
        )
      ).saveAndLoad(task)

      loaded.getValue(Assignee).displayName shouldBe task.getValue(Assignee).displayName
      loaded.getValue(MspField.taskDuration) shouldBe task.getValue(MspField.taskDuration)
      loaded.getValue(MspField.mustStartOn) shouldBe task.getValue(MspField.mustStartOn)
      loaded.getValue(MspField.taskWork) shouldBe task.getValue(MspField.taskWork)
      loaded.getValue(MspField.finish) shouldBe task.getValue(MspField.finish)
      loaded.getValue(MspField.deadline) shouldBe task.getValue(MspField.deadline)
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
    task1.getValue(Assignee).displayName shouldBe "alex"
    task1.getValue(MspField.taskDuration) shouldBe 12f
    val expectedStartDate: Date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/11/2013 08:00")
    assertEquals(expectedStartDate, task1.getValue(MspField.startAsSoonAsPossible))
    val expectedFinishDate: Date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/12/2013 12:00")
    assertEquals(expectedFinishDate, task1.getValue(MspField.finish))
  }

  def getConnector(folder: File): MSPConnector = {
    val file = new File(folder, MSP_FILE_NAME)
    val setup = FileSetup(MSPConnector.ID, Some("file"), "label", file.getAbsolutePath, file.getAbsolutePath)
    new MSPConnector(setup)
  }
}
