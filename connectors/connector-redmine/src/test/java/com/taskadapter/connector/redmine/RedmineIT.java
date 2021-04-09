package com.taskadapter.connector.redmine;

import com.taskadapter.connector.TestFieldBuilder;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTestChecks;
import com.taskadapter.connector.testlib.DateUtils;
import com.taskadapter.connector.testlib.FieldRowBuilder;
import com.taskadapter.connector.testlib.FieldWithValue;
import com.taskadapter.connector.testlib.ITFixture;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.internal.Transport;
import org.apache.http.client.HttpClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class RedmineIT {
    private static final Logger logger = LoggerFactory.getLogger(RedmineIT.class);

    private static final WebConnectorSetup serverInfo = RedmineTestConfig.getRedmineServerInfo();

    private static ITFixture fixture;
    private static RedmineManager mgr;
    private static String projectKey;
    private static HttpClient httpClient;
    private static User redmineUser;
    private static Transport transport;

    @BeforeClass
    public static void beforeAll() throws RedmineException {
        logger.info("Running Redmine tests with: " + serverInfo);

        httpClient = RedmineManagerFactory.createRedmineHttpClient(serverInfo.getHost());
        mgr = RedmineManagerFactory.createRedmineManager(serverInfo, httpClient);
        transport = mgr.getTransport();
        redmineUser = mgr.getUserManager().getCurrentUser();
    }

    @AfterClass
    public static void afterAll() throws RedmineException {
        if (mgr != null) {
            mgr.getProjectManager().deleteProject(projectKey);
            logger.info("Deleted temporary Redmine project with ID " + projectKey);
        }
        httpClient.getConnectionManager().shutdown();
    }

    @Before
    public void beforeEachTest() throws RedmineException {
        var junitTestProject = new Project(transport, "TA Redmine Integration test project",
                "test" + Calendar.getInstance().getTimeInMillis()).create();
        logger.info("Created temporary Redmine project with ID " + junitTestProject.getIdentifier());
        projectKey = junitTestProject.getIdentifier();

        var config = RedmineTestConfig.getRedmineTestConfig();
        config.setProjectKey(projectKey);

        fixture = new ITFixture(RedmineTestConfig.getRedmineServerInfo().getHost(), getConnector(config),
                CommonTestChecks.skipCleanup);
    }

    /**
     * it is important to check login name and not just display name because login name is resolved from [[RedmineUserCache]]
     */
    @Test
    public void assigneeLoginAndFullNameAreLoaded() throws ConnectorException, RedmineException {
        var task = GTaskBuilder.withSummary()
                .setValue(AllFields.assigneeFullName, redmineUser.getFullName());
        var config = getTestConfig();
        config.setFindUserByName(true);
        var connector = getConnector(config);
        var loadedTask = TestUtils.saveAndLoad(connector, task,
                FieldRowBuilder.rows(java.util.List.of(AllFields.summary, AllFields.assigneeFullName)));
        assertThat(loadedTask.getValue(AllFields.assigneeLoginName))
                .isEqualTo(redmineUser.getLogin());
        assertThat(loadedTask.getValue(AllFields.assigneeFullName))
                .isEqualTo(redmineUser.getFullName());
        var issue = new Issue().setId(loadedTask.getId().intValue());
        issue.setTransport(transport);
        issue.delete();
    }

    @Test
    public void taskIsCreatedWithChildren() throws ConnectorException {
        var t = new GTask();
        t.setId(1L);
        var summary = "generic task " + Calendar.getInstance().getTimeInMillis();
        t.setValue(AllFields.summary, summary);
        t.setValue(AllFields.description, "some descr" + Calendar.getInstance().getTimeInMillis() + "1");

        Float hours = (float) new Random().nextInt(50) + 1;
        t.setValue(AllFields.estimatedTime, hours);

        var c1 = new GTask();
        c1.setId(3L);
        var parentIdentity = new TaskId(1L, "1");
        c1.setParentIdentity(parentIdentity);
        c1.setValue(AllFields.summary, "Child 1 of " + summary);
        t.addChildTask(c1);

        var c2 = new GTask();
        c2.setId(4L);
        c2.setParentIdentity(parentIdentity);
        c2.setValue(AllFields.summary, "Child 2 of " + summary);
        t.addChildTask(c2);

        var loadedTasks = TestUtils.saveAndLoadAll(getConnector(), t, TestFieldBuilder.getSummaryRow());

        var tree = TreeUtils.buildTreeFromFlatList(loadedTasks);

        assertThat(tree).hasSize(1);

        var parent = tree.get(0);
        assertThat(parent.getChildren()).hasSize(2);
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

    @Test
    public void taskIsCreatedAndLoaded() {
        fixture.taskIsCreatedAndLoaded(GTaskBuilder.withSummary()
                        .setValue(AllFields.description, "123")
                        .setValue(AllFields.estimatedTime, 120F)
                        .setValue(AllFields.dueDate, DateUtils.nextYear())
                        .setValue(AllFields.startDate, DateUtils.yearAgo())
                        .setValue(AllFields.taskStatus, "New"),
                java.util.List.of(AllFields.startDate, AllFields.summary, AllFields.description, AllFields.dueDate,
                        AllFields.estimatedTime, AllFields.taskStatus));
    }

    @Test
    public void tasksCreatedWithoutErrors() {
        CommonTestChecks.createsTasks(getConnector(), TestFieldBuilder.getSummaryRow(), GTaskBuilder.getTwo(),
                CommonTestChecks.skipCleanup);
    }

    @Test
    public void taskIsUpdated() {
        fixture.taskCreatedAndUpdatedOK(GTaskBuilder.withSummary(),
                List.of(new FieldWithValue(AllFields.summary, "new value"),
                        new FieldWithValue(AllFields.taskStatus, findAnyNonDefaultTaskStatus())
                )
        );
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
    private RedmineConfig getTestConfig() {
        var config = RedmineTestConfig.getRedmineTestConfig();
        config.setProjectKey(projectKey);
        return config;
    }

    private RedmineConnector getConnector() {
        return getConnector(getTestConfig());
    }

    private RedmineConnector getConnector(RedmineConfig config) {
        return new RedmineConnector(config, RedmineTestConfig.getRedmineServerInfo());
    }

    private static String findDefaultTaskStatus() throws RedmineException {
        return mgr.getIssueManager().getStatuses()
                .stream().filter(IssueStatus::isDefaultStatus)
                .findFirst()
                .map(IssueStatus::getName)
                .orElse(null);
    }

    private static String findAnyNonDefaultTaskStatus() {
        // somehow they are all marked as "not default"
//    var statuses = mgr.getIssueManager.getStatuses
//    statuses.find(!_.isDefaultStatus).map(_.getName).orNull
        return "In Progress";
    }
}
