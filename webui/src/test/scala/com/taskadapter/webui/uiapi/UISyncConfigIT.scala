package com.taskadapter.webui.uiapi

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.jira.JiraField
import com.taskadapter.core.TaskKeeper
import com.taskadapter.model.GTask
import com.taskadapter.web.uiapi.ConfigLoader
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class UISyncConfigIT extends FunSpec with Matchers with TempFolder {
  /*  private var config = ConfigLoader.loadConfig("Redmine_Microsoft-Project_3.ta_conf")
    private var toRedmineConfig = config.reverse

    it("tasksCanBeSavedToRedmine") {
      val gTasks = List(new GTaskBuilder().withRandom(RedmineField.summary).build()).asJava
      val taskExportResult = toRedmineConfig.saveTasks(gTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      val saveResult = taskExportResult.saveResult
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.getCreatedTasksNumber).isEqualTo(1)
      assertThat(saveResult.getUpdatedTasksNumber).isEqualTo(0)
    }

    it("task With Description Unselected Is Created In Redmine With Empty Description") {
      val configWithDescriptionUnselected = ConfigLoader.loadConfig("Redmine_Redmine_description_unselected.ta_conf")
      val gTasks = List(new GTaskBuilder().withRandom(RedmineField.summary).build()).asJava
      val taskExportResult = configWithDescriptionUnselected.saveTasks(gTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      val saveResult = taskExportResult.saveResult
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.getCreatedTasksNumber).isEqualTo(1)
      assertThat(saveResult.getUpdatedTasksNumber).isEqualTo(0)
      val redmineConfig = new RedmineConfig
      // TODO get config from the same conf file as above
      redmineConfig.setServerInfo(new WebServerInfo("http://dev.taskadapter.com/redmine", "user", "123ZC"))
      val connector = new RedmineConnector(redmineConfig)
      val rows = List(FieldRow(true, JiraField.summary, RedmineField.summary, ""), FieldRow(true, JiraField.description, RedmineField.description, ""))
      val loaded = TestUtils.loadCreatedTask(connector, rows.asJava, saveResult)
      assertThat(loaded.getValue(RedmineField.description)).isEqualTo("")
    }
  */
  it("tasks can be loaded from JIRA and saved to Redmine") {
    withTempFolder { f =>
      val config = ConfigLoader.loadConfig(f, new TaskKeeper, "Atlassian-JIRA_Redmine.ta_conf")
      val loadedTasks = config.loadTasks(100)
      assertThat(loadedTasks.size).isGreaterThan(0)
      val saveResult = config.saveTasks(loadedTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.getCreatedTasksNumber).isEqualTo(loadedTasks.size)
    }
  }

  it("empty description field name on right side is ignored if selected=false") {
    withTempFolder { f =>
      val config = ConfigLoader.loadConfig(f, new TaskKeeper, "JIRA_Redmine_empty_description_on_right_side.ta_conf")
      val loadedTasks = config.loadTasks(100)
      assertThat(loadedTasks.size).isGreaterThan(0)
      val saveResult = config.saveTasks(loadedTasks, ProgressMonitorUtils.DUMMY_MONITOR)
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.getCreatedTasksNumber).isEqualTo(loadedTasks.size)
    }
  }

  it("fake JIRA task is created, then updated in Redmine") {
    withTempFolder { f =>
      val config = ConfigLoader.loadConfig(f, new TaskKeeper, "Atlassian-JIRA_Redmine.ta_conf")
      val jiraTask = new GTask
      jiraTask.setId(1l)
      jiraTask.setKey("TEST-66")
      jiraTask.setValue(JiraField.summary, "summary")

      val list = List(jiraTask).asJava
      val saveResult = config.saveTasks(list, ProgressMonitorUtils.DUMMY_MONITOR)
      assertThat(saveResult.hasErrors).isFalse()
      assertThat(saveResult.getCreatedTasksNumber).isEqualTo(1)
      assertThat(saveResult.getUpdatedTasksNumber).isEqualTo(0)

      val updateResult = config.saveTasks(list, ProgressMonitorUtils.DUMMY_MONITOR)
      assertThat(updateResult.hasErrors).isFalse()
      assertThat(updateResult.getCreatedTasksNumber).isEqualTo(0)
      assertThat(updateResult.getUpdatedTasksNumber).isEqualTo(1)
    }
  }

  /**
    * regression test for https://bitbucket.org/taskadapter/taskadapter/issues/43/tasks-are-not-updated-in-redmine-404-not
    */
  /*  it("taskWithRemoteIdIsUpdatedInRedmine") {
      val toRedmineConfig = config.reverse
      trySaveAndThenUpdate(toRedmineConfig)
    }

    it("taskWithRemoteIdIsUpdatedInMantisBT") {
      val config = ConfigLoader.loadConfig("Microsoft-Project_Mantis_1.ta_conf")
      trySaveAndThenUpdate(config)
    }

    it("taskWithRemoteIdIsUpdatedInJIRA") {
      val jiraMspConfig = ConfigLoader.loadConfig("Atlassian-Jira_Microsoft-Project_3.ta_conf")
      val toJIRAConfig = jiraMspConfig.reverse
      trySaveAndThenUpdate(toJIRAConfig)
    }

    it("taskWithRemoteIdIsUpdatedInGitHub") {
      val config = ConfigLoader.loadConfig("Github_Microsoft-Project_1.ta_conf")
      val reversedConfig = config.reverse
      trySaveAndThenUpdate(reversedConfig)
    }

    private def trySaveAndThenUpdate(uiSyncConfig: UISyncConfig) = {
      val summaryFieldName = "Summary"
      // TODO TA3 use connector-specific field
      val task = new GTaskBuilder().withRandom(summaryFieldName).build()
      val tasks = new util.ArrayList[GTask]
      tasks.add(task)
      val taskExportResult = uiSyncConfig.saveTasks(tasks, ProgressMonitorUtils.DUMMY_MONITOR)
      val saveResult = taskExportResult.saveResult
      val key = saveResult.getRemoteKeys.iterator.next
      val createdTask = tasks.get(0)
      createdTask.setRemoteId(key)
      createdTask.setValue(summaryFieldName, "updated summary")
      val secondResultWrapper = uiSyncConfig.saveTasks(tasks, ProgressMonitorUtils.DUMMY_MONITOR)
      val secondResult = secondResultWrapper.saveResult
      assertThat(secondResult.hasErrors).isFalse()
      assertThat(secondResult.getCreatedTasksNumber).isEqualTo(0)
      assertThat(secondResult.getUpdatedTasksNumber).isEqualTo(1)
    }*/
}