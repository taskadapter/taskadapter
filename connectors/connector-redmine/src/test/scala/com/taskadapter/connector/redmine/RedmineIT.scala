package com.taskadapter.connector.redmine

import java.util.Calendar

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.TreeUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib._
import com.taskadapter.model.{GTask, GUser}
import com.taskadapter.redmineapi.bean.ProjectFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.Random

trait TempRedmineProject {
  val logger = LoggerFactory.getLogger(classOf[TempRedmineProject])
  val serverInfo = RedmineTestConfig.getRedmineServerInfo
  logger.info("Running Redmine tests with: " + serverInfo)

  val junitTestProject = ProjectFactory.create("TA Redmine Integration test project", "test" + Calendar.getInstance.getTimeInMillis)

  val httpClient = RedmineManagerFactory.createRedmineHttpClient()
  var mgr = RedmineManagerFactory.createRedmineManager(serverInfo, httpClient)
  val redmineUser = mgr.getUserManager.getCurrentUser
  val currentUser = RedmineToGUser.convertToGUser(redmineUser)
  val createdProject = mgr.getProjectManager.createProject(junitTestProject)
  logger.info("Created temporary Redmine project with ID " + junitTestProject.getIdentifier)
  var projectKey = createdProject.getIdentifier
}

@RunWith(classOf[JUnitRunner])
class RedmineIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with TempRedmineProject {

  override def afterAll() {
    if (mgr != null) {
      mgr.getProjectManager.deleteProject(projectKey)
      logger.info("Deleted temporary Redmine project with ID " + projectKey)
    }
    httpClient.getConnectionManager.shutdown()
  }

  /*
     public void startDateExported() throws ConnectorException {
         checkStartDate(getTestSaverWith(RedmineField.startDate()), TestUtils.getYearAgo())
     }
 */

  private def getTestSaver(rows: List[FieldRow]) = new TestSaver(getConnector(), rows)

  /*    private TestSaver getTestSaver() {
          return new TestSaver(getConnector(), RedmineFieldBuilder.getDefault())
      }*/

  private def getTestSaver(config: RedmineConfig, rows: List[FieldRow]) = new TestSaver(getConnector(config), rows)

  /*
      private void checkStartDate(TestSaver testSaver, Date expected) throws ConnectorException {
          GTask task = TestUtils.generateTask()
          Date yearAgo = TestUtils.getYearAgo()
          task.setStartDate(yearAgo)
          GTask loadedTask = testSaver.saveAndLoad(task)
          assertEquals(expected, loadedTask.getStartDate())
      }

      @Test
      public void dueDateExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task)
          GTask loadedTask = getTestSaverWith(RedmineField.dueDate()).saveAndLoad(task)
          assertEquals(yearAgo.getTime(), loadedTask.getDueDate())
      }
*/
  it("exports assignee") {
    val task = RedmineGTaskBuilder.withSummary()
    task.setValue(RedmineField.assignee, currentUser)
    val config = getTestConfig
    config.setFindUserByName(true)
    val connector = getConnector(config)
    val loadedTask = TestUtils.saveAndLoad(connector, task, RedmineFieldBuilder.withAssignee())
    loadedTask.getValue(RedmineField.assignee).asInstanceOf[GUser].getDisplayName shouldBe currentUser.getDisplayName
    mgr.getIssueManager.deleteIssue(loadedTask.getId.toInt)
  }

  /*
      @Test
      public void estimatedTimeExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          GTask loadedTask = getTestSaverWith(RedmineField.estimatedTime()).saveAndLoad(task)
          assertEquals(task.getValue(RedmineField.estimatedTime()), loadedTask.getValue(RedmineField.estimatedTime()))
      }
  */

  private def findDefaultTaskStatus(): String = {
    mgr.getIssueManager.getStatuses.asScala.find(_.isDefaultStatus).map(_.getName).orNull
  }

  private def findAnyNonDefaultTaskStatus(): String = {
    mgr.getIssueManager.getStatuses.asScala.find(!_.isDefaultStatus).map(_.getName).orNull
  }

  /*
      //temporary ignored (need to add ProjectMemberships to redmine-java-api)
      @Ignore
      @Test
      public void taskStatusExported() throws Exception {
          String otherStatus = getOtherTaskStatus()

          if (otherStatus != null) {
              GTask task = TestUtils.generateTask()
              task.setValue(RedmineField.taskStatus(), otherStatus)
              GTask loadedTask = getTestSaver().selectField(FIELD.TASK_STATUS).saveAndLoad(task)
              assertEquals(otherStatus, loadedTask.getValue(RedmineField.taskStatus()))
          }
      }
*/

  it("task is created with children") {
    val t = new GTask()
    t.setId(1l)
    val summary = "generic task " + Calendar.getInstance().getTimeInMillis
    t.setValue(RedmineField.summary, summary)
    t.setValue(RedmineField.description, "some descr" + Calendar.getInstance().getTimeInMillis + "1")

    val hours = Random.nextInt(50) + 1;
    t.setValue(RedmineField.estimatedTime, hours)

    val c1 = new GTask()
    c1.setId(3l)
    val parentIdentity = TaskId(1, "1")
    c1.setParentIdentity(parentIdentity)
    c1.setValue(RedmineField.summary, "Child 1 of " + summary)
    t.addChildTask(c1)

    val c2 = new GTask()
    c2.setId(4l)
    c2.setParentIdentity(parentIdentity)
    c2.setValue(RedmineField.summary, "Child 2 of " + summary)
    t.addChildTask(c2)

    val loadedTasks = TestUtils.saveAndLoadAll(getConnector(), t, RedmineFieldBuilder.getDefault())

    val tree = TreeUtils.buildTreeFromFlatList(loadedTasks.asJava)

    tree.size() shouldBe 1

    val parent = tree.get(0)
    parent.getChildren.size() shouldBe 2
  }
  /*
        @Test
        public void taskExportedWithoutRelations() throws Exception {
            RedmineConfig config = getTestConfig()
            config.setSaveIssueRelations(false)
            GTask loadedTask = createTaskWithPrecedesRelations(getConnector(config), 2, TestMappingUtils.fromFields(SUPPORTED_FIELDS))
  
            assertEquals(0, loadedTask.getRelations().size())
        }
  
        @Test
        public void taskExportedWithRelations() throws Exception {
            RedmineConfig config = getTestConfig()
            config.setSaveIssueRelations(true)
            GTask loadedTask = createTaskWithPrecedesRelations(getConnector(config), 2, TestMappingUtils.fromFields(SUPPORTED_FIELDS))
  
            assertEquals(2, loadedTask.getRelations().size())
        }
    *//*
  
        @Test
        public void notMappedDescriptionIsSetToEmpty() throws Exception {
            GTask task = TestUtils.generateTask()
            GTask loadedTask = getTestSaver().unselectField(FIELD.DESCRIPTION).saveAndLoad(task)
            assertEquals("", loadedTask.getDescription())
        }
    *//*
        @Test
        public void taskUpdateTaskWithDeletedRelation() throws Exception {
            RedmineConfig config = getTestConfig()
            config.setSaveIssueRelations(true)
            Mappings mapping = TestMappingUtils.fromFields(SUPPORTED_FIELDS)
            RedmineConnector connector = getConnector(config)
            GTask loadedTask = createTaskWithPrecedesRelations(connector, 2, mapping)
  
            ArrayList<GTask> taskList = new ArrayList<>(3)
            loadedTask.setSourceSystemId(loadedTask.getKey())
            taskList.add(loadedTask)
  
            GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey(), mapping)
            task.setSourceSystemId(task.getKey())
            taskList.add(task)
  
            task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey(), mapping)
            task.setSourceSystemId(task.getKey())
            taskList.add(task)
  
            loadedTask.getRelations().remove(0)
            TestUtils.saveAndLoadList(connector, taskList, mapping)
            GTask newTask = connector.loadTaskByKey(loadedTask.getKey(), mapping)
  
            assertEquals(1, newTask.getRelations().size())
        }
  
        @Test
        public void taskUpdateTaskWithCreatedRelation() throws Exception {
            RedmineConfig config = getTestConfig()
            config.setSaveIssueRelations(true)
            Mappings mapping = TestMappingUtils.fromFields(SUPPORTED_FIELDS)
            RedmineConnector connector = getConnector(config)
            GTask loadedTask = createTaskWithPrecedesRelations(connector, 2, mapping)
  
            ArrayList<GTask> taskList = new ArrayList<>(3)
            loadedTask.setSourceSystemId(loadedTask.getKey())
            taskList.add(loadedTask)
  
            GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey(), mapping)
            task.setSourceSystemId(task.getKey())
            taskList.add(task)
  
            task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey(), mapping)
            task.setSourceSystemId(task.getKey())
            taskList.add(task)
  
            GTask t = TestUtils.generateTask()
            GTask newTask = TestUtils.saveAndLoad(connector, t, mapping)
            newTask.setSourceSystemId(newTask.getKey())
            taskList.add(newTask)
  
            loadedTask.getRelations().add(new GRelation(loadedTask.getRemoteId(), newTask.getKey(), GRelation.TYPE.precedes))
            TestUtils.saveAndLoadList(connector, taskList, mapping)
            newTask = connector.loadTaskByKey(loadedTask.getKey(), mapping)
  
            assertEquals(3, newTask.getRelations().size())
        }
*/

  it("task is created and loaded") {
    CommonTestChecks.taskIsCreatedAndLoaded(getConnector(), RedmineGTaskBuilder.withSummary(),
      RedmineFieldBuilder.getDefault(), RedmineField.summary,
      CommonTestChecks.skipCleanup)
  }

  /*
          @Test
          public void defaultDescriptionIsMapped() throws Exception {
              CommonTests.descriptionSavedByDefault(getConnector(), TestMappingUtils.fromFields(SUPPORTED_FIELDS))
          }

          @Test
          public void descriptionSavedIfSelected() throws Exception {
              CommonTests.descriptionSavedIfSelected(getConnector(), TestMappingUtils.fromFields(SUPPORTED_FIELDS))
          }
    */
  it("tasks are created without errors") {
    CommonTestChecks.createsTasks(getConnector(), RedmineFieldBuilder.getDefault(), RedmineGTaskBuilder.getTwo(),
      CommonTestChecks.skipCleanup)
  }


  it("task is updated") {
    CommonTestChecks.taskCreatedAndUpdatedOK(RedmineTestConfig.getRedmineServerInfo.host,
      getConnector(), RedmineFieldBuilder.getDefault(),
      RedmineGTaskBuilder.withSummary(), RedmineField.summary.name, CommonTestChecks.skipCleanup)
  }

  /*
        private static GTask createTaskWithPrecedesRelations(RedmineConnector redmine, Integer childCount, List<FieldRow> rows) throws ConnectorException {
            List<GTask> list = new ArrayList<>()

            GTask task = TestUtils.generateTask()
            task.setId(1)
            list.add(task)

            for (int i = 0; i < childCount; i++) {
                GTask task1 = TestUtils.generateTask()
                task1.setId(i + 2)

                task.getRelations().add(new GRelation(task.getId().toString(), task1.getId().toString(), GRelation.TYPE.precedes))
                list.add(task1)
            }
            List<GTask> loadedList = TestUtils.saveAndLoadList(redmine, list, rows)
            return TestUtils.findTaskBySummary(loadedList, task.getSummary())
        }
    */
  private def getTestConfig = {
    val config = RedmineTestConfig.getRedmineTestConfig
    config.setProjectKey(projectKey)
    config
  }

  private def getConnector(): RedmineConnector = getConnector(getTestConfig)

  private def getConnector(config: RedmineConfig) = new RedmineConnector(config, RedmineTestConfig.getRedmineServerInfo)
}
