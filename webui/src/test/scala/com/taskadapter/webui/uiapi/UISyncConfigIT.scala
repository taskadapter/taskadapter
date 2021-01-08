package com.taskadapter.webui.uiapi

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskId
//import com.taskadapter.connector.github.GithubField
import com.taskadapter.connector.jira.JiraField
//import com.taskadapter.connector.mantis.MantisField
import com.taskadapter.model.{Description, Field, GTask, GTaskBuilder, Summary}
import com.taskadapter.web.uiapi.{ConfigLoader, UISyncConfig}
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class UISyncConfigIT extends FunSpec with Matchers with ConfigsTempFolder {
  // TODO TA3 ui-sync-level tests
  /*  private var config = ConfigLoader.loadConfig("Redmine_Microsoft-Project_3.conf")
    private var toRedmineConfig = config.reverse

    it("tasksCanBeSavedToRedmine") {
      val gTasks = List(new GTaskBuilder().withRandom(RedmineField.summary).build()).asJava
      val taskExportResult = toRedmineConfig.saveTasks(gTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      val saveResult = taskExportResult.saveResult
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.createdTasksNumber).isEqualTo(1)
      assertThat(saveResult.updatedTasksNumber).isEqualTo(0)
    }

    it("task With Description Unselected Is Created In Redmine With Empty Description") {
      val configWithDescriptionUnselected = ConfigLoader.loadConfig("Redmine_Redmine_description_unselected.conf")
      val gTasks = List(new GTaskBuilder().withRandom(RedmineField.summary).build()).asJava
      val taskExportResult = configWithDescriptionUnselected.saveTasks(gTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      val saveResult = taskExportResult.saveResult
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.createdTasksNumber).isEqualTo(1)
      assertThat(saveResult.updatedTasksNumber).isEqualTo(0)
      val redmineConfig = new RedmineConfig
      // TODO get config from the same conf file as above
      redmineConfig.setServerInfo(...)
      val connector = new RedmineConnector(redmineConfig)
      val rows = List(FieldRow(true, JiraField.summary, RedmineField.summary, ""), FieldRow(true, JiraField.description, RedmineField.description, ""))
      val loaded = TestUtils.loadCreatedTask(connector, rows.asJava, saveResult)
      assertThat(loaded.getValue(RedmineField.description)).isEqualTo("")
    }
  */

  // TODO this test requires some "Epic" tasks to be present in JIRA. it should create them itself
  it("tasks loaded from JIRA and saved to Redmine") {
    withTempFolder { f =>
      val config = ConfigLoader.loadConfig(f, "Atlassian-JIRA_Redmine.conf")
      val loadedTasks = UISyncConfig.loadTasks(config, 100)
      assertThat(loadedTasks.size).isGreaterThan(0)
      val saveResult = config.saveTasks(loadedTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      saveResult.hasErrors shouldBe false
      saveResult.createdTasksNumber shouldBe loadedTasks.size
    }
  }

  it("empty description field name on right side is ignored if selected=false") {
    withTempFolder { f =>
      val config = ConfigLoader.loadConfig(f, "JIRA_Redmine_empty_description_on_right_side.conf")
      val loadedTasks = UISyncConfig.loadTasks(config, 100)
      assertThat(loadedTasks.size).isGreaterThan(0)
      val saveResult = config.saveTasks(loadedTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.createdTasksNumber).isEqualTo(loadedTasks.size)
    }
  }

  it("fake JIRA task is created, then updated in Redmine") {
    withTempFolder { f =>
      val config = ConfigLoader.loadConfig(f, "Atlassian-JIRA_Redmine.conf")
      val jiraTask = new GTask
      jiraTask.setId(66l)
      jiraTask.setKey("TEST-66")
      jiraTask.setValue(Summary, "summary")

      val list = List(jiraTask).asJava
      val saveResult = config.saveTasks(list, ProgressMonitorUtils.DUMMY_MONITOR)
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.createdTasksNumber).isEqualTo(1)
      assertThat(saveResult.updatedTasksNumber).isEqualTo(0)

      // now pretend that the task was loaded from somewhere
      jiraTask.setSourceSystemId(TaskId(66, "TEST-66"))
      val updateResult = config.saveTasks(list, ProgressMonitorUtils.DUMMY_MONITOR)
      assertThat(updateResult.hasErrors).isFalse()
      assertThat(updateResult.createdTasksNumber).isEqualTo(0)
      assertThat(updateResult.updatedTasksNumber).isEqualTo(1)
    }
  }

    it("taskWithRemoteIdIsUpdatedInMantisBT") {
      withTempFolder { f =>
        val config = ConfigLoader.loadConfig(f, "Mantis_1-Microsoft-Project.conf")
        val reversed = config.reverse
        trySaveAndThenUpdate(reversed, Summary, Some(Description))
      }
    }

    it("taskWithRemoteIdIsUpdatedInJIRA") {
      withTempFolder { f =>
        val jiraMspConfig = ConfigLoader.loadConfig(f, "Atlassian-Jira_Microsoft-Project_3.conf")
        val toJIRAConfig = jiraMspConfig.reverse
        trySaveAndThenUpdate(toJIRAConfig, Summary)
      }
    }

    it("taskWithRemoteIdIsUpdatedInGitHub") {
      withTempFolder { f =>
        val config = ConfigLoader.loadConfig(f, "Github_Microsoft-Project_1.conf")
        val reversedConfig = config.reverse
        trySaveAndThenUpdate(reversedConfig, Summary)
      }
    }

    private def trySaveAndThenUpdate(uiSyncConfig: UISyncConfig, summaryField:Field[String]
                                    , secondField: Option[Field[String]] = None) = {
      val builder = new GTaskBuilder().withRandom(summaryField)
      val task = builder.build()
      task.setId(123l)
      task.setKey("123")
      task.setSourceSystemId(TaskId(123, "123"))
      if (secondField.isDefined) {
        task.setValue(secondField.get, "some value")
      }
      val tasks = List(task).asJava
      val firstResult = uiSyncConfig.saveTasks(tasks, ProgressMonitorUtils.DUMMY_MONITOR)
      firstResult.hasErrors shouldBe false
      firstResult.createdTasksNumber shouldBe 1
      firstResult.updatedTasksNumber shouldBe 0


      task.setValue(summaryField, "updated summary")
      val secondResult = uiSyncConfig.saveTasks(tasks, ProgressMonitorUtils.DUMMY_MONITOR)
      secondResult.hasErrors shouldBe false
      secondResult.createdTasksNumber shouldBe 0
      secondResult.updatedTasksNumber shouldBe 1
    }
}