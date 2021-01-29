package com.taskadapter.connector.redmine

import java.util.Calendar

import com.taskadapter.connector.common.TreeUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib._
import com.taskadapter.model._
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

  val httpClient = RedmineManagerFactory.createRedmineHttpClient(serverInfo.host)
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

  private val fixture = ITFixture(RedmineTestConfig.getRedmineServerInfo.host, getConnector(),
    id => CommonTestChecks.skipCleanup(id))

  /**
    * it is important to check login name and not just display name because login name is resolved from [[RedmineUserCache]]
    */
  it("assignee login and full name are loaded") {
    val task = GTaskBuilder.withSummary()
      .setValue(AssigneeFullName, redmineUser.getFullName)
    val config = getTestConfig
    config.setFindUserByName(true)
    val connector = getConnector(config)
    val loadedTask = TestUtilsJava.saveAndLoad(connector, task, FieldRowBuilder.rows(Seq(Summary, AssigneeFullName)).asJava)
    loadedTask.getValue(AssigneeLoginName) shouldBe redmineUser.getLogin
    loadedTask.getValue(AssigneeFullName) shouldBe redmineUser.getFullName
    mgr.getIssueManager.deleteIssue(loadedTask.getId.toInt)
  }

  it("task is created with children") {
    val t = new GTask()
    t.setId(1l)
    val summary = "generic task " + Calendar.getInstance().getTimeInMillis
    t.setValue(Summary, summary)
    t.setValue(Description, "some descr" + Calendar.getInstance().getTimeInMillis + "1")

    val hours: Integer = Random.nextInt(50) + 1
    t.setValue(EstimatedTime, hours.toFloat)

    val c1 = new GTask()
    c1.setId(3l)
    val parentIdentity = TaskId(1, "1")
    c1.setParentIdentity(parentIdentity)
    c1.setValue(Summary, "Child 1 of " + summary)
    t.addChildTask(c1)

    val c2 = new GTask()
    c2.setId(4l)
    c2.setParentIdentity(parentIdentity)
    c2.setValue(Summary, "Child 2 of " + summary)
    t.addChildTask(c2)

    val loadedTasks = TestUtils.saveAndLoadAll(getConnector(), t, RedmineFieldBuilder.getDefault().asJava)

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
    fixture.taskIsCreatedAndLoaded(GTaskBuilder.withSummary()
      .setValue(Description, "123")
      .setValue(EstimatedTime, 120f)
      .setValue(DueDate, TestUtils.nextYear)
      .setValue(StartDate, TestUtils.yearAgo)
      .setValue(TaskStatus, "New")
      ,
      Seq(StartDate, Summary, Description, DueDate, EstimatedTime, TaskStatus))
  }

  it("tasks are created without errors") {
    CommonTestChecks.createsTasks(getConnector(), RedmineFieldBuilder.getDefault(), GTaskBuilder.getTwo(),
      CommonTestChecks.skipCleanup)
  }


  it("task is updated") {
    fixture.taskCreatedAndUpdatedOK(GTaskBuilder.withSummary(),
      Seq((Summary, "new value"),
        (TaskStatus, findAnyNonDefaultTaskStatus())
      )
    )
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

  private def findDefaultTaskStatus(): String = {
    mgr.getIssueManager.getStatuses.asScala.find(_.isDefaultStatus).map(_.getName).orNull
  }

  private def findAnyNonDefaultTaskStatus(): String = {
    // somehow they are all marked as "not default"
//    val statuses = mgr.getIssueManager.getStatuses
//    statuses.asScala.find(!_.isDefaultStatus).map(_.getName).orNull
    "In Progress"
  }

}
