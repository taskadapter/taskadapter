package com.taskadapter.connector.redmine

import java.util
import java.util.Calendar

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.TreeUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib._
import com.taskadapter.core.TaskKeeper
import com.taskadapter.model.GTask
import com.taskadapter.redmineapi.bean.ProjectFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.Random

@RunWith(classOf[JUnitRunner])
trait TempRedmineProject {
  val logger = LoggerFactory.getLogger(classOf[TempRedmineProject])
  val serverInfo = RedmineTestConfig.getRedmineServerInfo
  logger.info("Running Redmine tests with: " + serverInfo)

  val junitTestProject = ProjectFactory.create("TA Redmine Integration test project", "test" + Calendar.getInstance.getTimeInMillis)

  var mgr = RedmineManagerFactory.createRedmineManager(serverInfo)
  val redmineUser = mgr.getUserManager.getCurrentUser
  val currentUser = RedmineToGUser.convertToGUser(redmineUser)
  val createdProject = mgr.getProjectManager.createProject(junitTestProject)
  logger.info("Created temporary Redmine project with ID " + junitTestProject.getIdentifier)
  var projectKey = createdProject.getIdentifier
}

class RedmineIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with TempRedmineProject {

  override def afterAll() {
    if (mgr != null) {
      mgr.getProjectManager.deleteProject(projectKey)
      logger.info("Deleted temporary Redmine project with ID " + projectKey)
    }
  }

  /*
     @Test
     public void startDateNotExported() throws ConnectorException {
         checkStartDate(getTestSaverWith(RedmineField.startDate()), null)
     }

     @Test
     public void startDateExported() throws ConnectorException {
         checkStartDate(getTestSaverWith(RedmineField.startDate()), TestUtils.getYearAgo())
     }

     @Test
     public void startDateExportedByDefault() throws ConnectorException {
         checkStartDate(getTestSaver(), TestUtils.getYearAgo())
     }

     private TestSaver getTestSaverWith(String field) {
         return getTestSaver(RedmineFieldBuilder.withField(field, ""))
     }
 */

  private def getTestSaver(rows: util.List[FieldRow]) = new TestSaver(getConnector(), rows)

  /*    private TestSaver getTestSaver() {
          return new TestSaver(getConnector(), RedmineFieldBuilder.getDefault())
      }*/

  private def getTestSaver(config: RedmineConfig, rows: util.List[FieldRow]) = new TestSaver(getConnector(config), rows)

  /*
      private void checkStartDate(TestSaver testSaver, Date expected) throws ConnectorException {
          GTask task = TestUtils.generateTask()
          Date yearAgo = TestUtils.getYearAgo()
          task.setStartDate(yearAgo)
          GTask loadedTask = testSaver.saveAndLoad(task)
          assertEquals(expected, loadedTask.getStartDate())
      }

      @Test
      public void dueDateNotExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          TestUtils.setTaskDueDateNextYear(task)
          GTask loadedTask = getTestSaver().unselectField(FIELD.DUE_DATE).saveAndLoad(task)
          assertNull(loadedTask.getDueDate())
      }

      @Test
      public void dueDateExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task)
          GTask loadedTask = getTestSaverWith(RedmineField.dueDate()).saveAndLoad(task)
          assertEquals(yearAgo.getTime(), loadedTask.getDueDate())
      }

      @Test
      public void dueDateExportedByDefault() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task)
          GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS))
          assertEquals(yearAgo.getTime(), loadedTask.getDueDate())
      }

      @Test
      public void assigneeExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          task.setValue(RedmineField.assignee(), currentUser)
          GTask loadedTask = getTestSaverWith(RedmineField.assignee()).saveAndLoad(task)
          User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee())
          assertEquals(currentUser.getId(), loadedAssignee.getId())
          // only the ID and Display Name are set, so we can't check login name
          assertEquals(currentUser.getDisplayName(), loadedAssignee.getFullName())
      }

      @Test
      public void assigneeNotExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          task.setValue(RedmineField.assignee(), currentUser)
          GTask loadedTask = getTestSaver().saveAndLoad(task)
          User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee())
          assertNull(loadedAssignee)
      }
  *//*
      @Test
      public void assigneeExportedByDefault() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          task.setAssignee(currentUser)
          GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, RFBTestMappingUtils.fromFields(SUPPORTED_FIELDS))
          User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee())
          assertEquals(currentUser.getId(), loadedAssignee.getId())
      }
  */
  // TODO what does it test?? how is findByName related to Assignee export?
  /*
      @Test
      public void assigneeExportedByName() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          task.setAssignee(currentUser)

          RedmineConfig config = getTestConfig()
          config.setFindUserByName(true)
          GTask loadedTask = getTestSaver(config, RedmineFieldBuilder.withField(RedmineField.assignee(), "")).saveAndLoad(task)
          User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee())
          assertEquals(currentUser.getId(), loadedAssignee.getId())
          // only the ID and Display Name are set, so we can't check login name
          assertEquals(currentUser.getDisplayName(), loadedAssignee.getFullName())
      }

  *//*
      @Test
      public void estimatedTimeNotExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          GTask loadedTask = getTestSaver().unselectField(FIELD.ESTIMATED_TIME).saveAndLoad(task)
          assertNull(loadedTask.getEstimatedHours())
      }


      @Test
      public void estimatedTimeExported() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          GTask loadedTask = getTestSaverWith(RedmineField.estimatedTime()).saveAndLoad(task)
          assertEquals(task.getValue(RedmineField.estimatedTime()), loadedTask.getValue(RedmineField.estimatedTime()))
      }

      @Test
      public void estimatedTimeExportedByDefault() throws ConnectorException {
          GTask task = TestUtils.generateTask()
          GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS))
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
      @Test
      public void taskStatusNotExported() throws RedmineException, ConnectorException {
          String defaultStatus = findDefaultTaskStatus()

          if (defaultStatus != null) {
              GTask task = TestUtils.generateTask()
              task.setValue(RedmineField.taskStatus(), "Resolved")
              GTask loadedTask = getTestSaver().unselectField(FIELD.TASK_STATUS).saveAndLoad(task)
              assertEquals(defaultStatus, loadedTask.getValue(RedmineField.taskStatus()))
          }
      }
  
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
  
      @Test
      public void taskStatusExportedByDefault() throws Exception {
          String defaultStatus = findDefaultTaskStatus()

          if (defaultStatus != null) {
              GTask task = TestUtils.generateTask()
              task.setValue(RedmineField.taskStatus(), null)
              GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS))
              assertEquals(defaultStatus, loadedTask.getValue(RedmineField.taskStatus()))
          }
      }
*/

  // TODO TA3  t.getChildren().add(c1) fails in Scala because of list conversions (list is immutable)
  ignore("task with children") {
    val t = new GTask()
    t.setId(1l)
    val summary = "generic task " + Calendar.getInstance().getTimeInMillis
    t.setValue(RedmineField.summary, summary)
    t.setValue(RedmineField.description, "some descr" + Calendar.getInstance().getTimeInMillis + "1")

    val hours = Random.nextInt(50) + 1;
    t.setValue(RedmineField.estimatedTime, hours)
    t.setChildren(List[GTask]().asJava)

    val c1 = new GTask()
    c1.setId(3l)
    val parentIdentity = TaskId(1, "1")
    c1.setParentIdentity(parentIdentity)
    c1.setValue(RedmineField.summary, "Child 1 of " + summary)
    t.getChildren().add(c1)

    val c2 = new GTask()
    c2.setId(4l)
    c2.setParentIdentity(parentIdentity)
    c2.setValue(RedmineField.summary, "Child 2 of " + summary)
    t.getChildren().add(c2)

    val loadedTasks = TestUtils.saveAndLoadAll(getConnector(), t, RedmineFieldBuilder.getDefault())

    val filtered = loadedTasks.filter(_.getValue(RedmineField.summary) == summary)

    val tree = TreeUtils.buildTreeFromFlatList(filtered.asJava)
        
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
            loadedTask.setRemoteId(loadedTask.getKey())
            taskList.add(loadedTask)
  
            GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey(), mapping)
            task.setRemoteId(task.getKey())
            taskList.add(task)
  
            task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey(), mapping)
            task.setRemoteId(task.getKey())
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
            loadedTask.setRemoteId(loadedTask.getKey())
            taskList.add(loadedTask)
  
            GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey(), mapping)
            task.setRemoteId(task.getKey())
            taskList.add(task)
  
            task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey(), mapping)
            task.setRemoteId(task.getKey())
            taskList.add(task)
  
            GTask t = TestUtils.generateTask()
            GTask newTask = TestUtils.saveAndLoad(connector, t, mapping)
            newTask.setRemoteId(newTask.getKey())
            taskList.add(newTask)
  
            loadedTask.getRelations().add(new GRelation(loadedTask.getRemoteId(), newTask.getKey(), GRelation.TYPE.precedes))
            TestUtils.saveAndLoadList(connector, taskList, mapping)
            newTask = connector.loadTaskByKey(loadedTask.getKey(), mapping)
  
            assertEquals(3, newTask.getRelations().size())
        }
  
  
        @Test
        public void someTasksAreLoaded() throws Exception {
            CommonTests.testLoadTasks(getConnector(), RedmineFieldBuilder.getDefault())
        }
  
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
    CommonTestChecks.createsTasks(getConnector(), RedmineFieldBuilder.getDefault().asJava, RedmineGTaskBuilder.getTwo().asJava)
  }


  it("task is updated") {
    CommonTestChecks.taskCreatedAndUpdatedOK(getConnector(), RedmineFieldBuilder.getDefault().asJava,
      RedmineGTaskBuilder.withSummary(), RedmineField.summary.name)
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
