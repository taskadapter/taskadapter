package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.google.common.collect.Iterables;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.TestFieldBuilder;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTestChecks;
import com.taskadapter.connector.testlib.FieldRowBuilder;
import com.taskadapter.connector.testlib.RandomStringGenerator;
import com.taskadapter.connector.testlib.StatefulTestTaskSaver;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomListString;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import com.taskadapter.test.core.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class JiraConnectorIT {
    private static final WebConnectorSetup webServerInfo = JiraPropertiesLoader.getTestServerInfo();
    private static final JiraConfig config = JiraPropertiesLoader.createTestConfig();
    private static JiraRestClient client;

    @BeforeClass
    public static void beforeAllTests() throws ConnectorException {
        client = JiraConnectionFactory.createClient(webServerInfo);
    }

    @Test
    public void tasksAreCreatedWithoutErrors() {
        CommonTestChecks.createsTasks(getConnector(),
                TestFieldBuilder.getSummaryAndAssigneeLogin(), GTaskBuilder.getTwo(),
                id -> {
                    TestJiraClientHelper.deleteTasks(client, id);
                    return null;
                });
    }

    @Test
    public void connectorDoesNotFailEmptyTasksListOnCreate() {
        getConnector().saveData(PreviouslyCreatedTasksResolver.empty,
                List.of(), ProgressMonitorUtils.DUMMY_MONITOR, TestFieldBuilder.getSummaryAndAssigneeLogin());
    }

    @Test
    public void testLoadTaskByKey() throws ConnectorException {
        var connector = getConnector();
        var summary = "load by key";
        var task = new JiraGTaskBuilder(summary).withType("Task").build();
        var id = TestUtils.save(connector, task, TestFieldBuilder.getSummaryAndAssigneeLogin());
        var loadedTask = connector.loadTaskByKey(id, TestFieldBuilder.getSummaryAndAssigneeLogin());
        assertThat(loadedTask.getValue(AllFields.summary)).isEqualTo(summary);
        TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity());
    }

    @Test
    public void descriptionSavedByDefault() {
        CommonTestChecks.fieldIsSavedByDefault(getConnector(),
                new JiraGTaskBuilder(RandomStringGenerator.randomAlphaNumeric(30))
                        .withDescription().build(),
                JiraField.defaultFieldsForNewConfig(),
                AllFields.description,
                taskId -> {
                    TestJiraClientHelper.deleteTasks(client, taskId);
                    return null;
                });
    }

    @Test
    public void subtasksAreCreated() throws Exception {
        var parentTask = new JiraGTaskBuilder("parent task").build();

        var subTask1 = new JiraGTaskBuilder("child task 1").build();
        var subTask2 = new JiraGTaskBuilder("child task 2").build();
        parentTask.getChildren().addAll(
                List.of(subTask1, subTask2));
        var connector = getConnector();
        var result = connector.saveData(PreviouslyCreatedTasksResolver.empty, Arrays.asList(parentTask),
                ProgressMonitorUtils.DUMMY_MONITOR,
                TestFieldBuilder.getSummaryAndAssigneeLogin());
        assertThat(result.getCreatedTasksNumber()).isEqualTo(3);
        var parentTaskId = result.getKeyToRemoteKeyList().get(0).getNewId();
        var subTask1Id = result.getKeyToRemoteKeyList().get(1).getNewId();
        var subTask2Id = result.getKeyToRemoteKeyList().get(2).getNewId();

        var loadedSubTask1 = connector.loadTaskByKey(subTask1Id, TestFieldBuilder.getSummaryAndAssigneeLogin());
        var loadedSubTask2 = connector.loadTaskByKey(subTask2Id, TestFieldBuilder.getSummaryAndAssigneeLogin());
        assertThat(loadedSubTask1.getParentIdentity()).isEqualTo(parentTaskId);
        assertThat(loadedSubTask2.getParentIdentity()).isEqualTo(parentTaskId);

        TestJiraClientHelper.deleteTasks(client, loadedSubTask1.getIdentity(), loadedSubTask2.getIdentity(), parentTaskId);
    }

    @Test
    public void assigneeIsSavedAndLoaded() throws ConnectorException {
        var task = new JiraGTaskBuilder(RandomStringGenerator.randomAlphaNumeric(30))
                .build()
                .setValue(AllFields.assigneeLoginName, "user");
        var id = TestUtils.save(getConnector(), task, TestFieldBuilder.getSummaryAndAssigneeLogin());
        var loadedTask = getConnector().loadTaskByKey(id, TestFieldBuilder.getSummaryAndAssigneeLogin());
        assertThat(loadedTask.getValue(AllFields.assigneeLoginName)).isEqualTo("user");
        TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity());
    }

    /*
     * This test requires a pre-created custom field in your JIRA.
     *
     * - name: custom_checkbox_1
     * - type: checkbox multi-select
     * - allowed values: "option1", "option2"
     */
    @Test
    public void taskIsCreatedWithMultiValueCustomFieldOfTypeOptionCheckboxes() throws ConnectorException {
        var customFieldName = "custom_checkbox_1";

        TestJiraClientHelper.checkCustomFieldExists(client, customFieldName);

        var task = GTaskBuilder.withSummary();
        var field = new CustomListString(customFieldName);
        task.setValue(field, List.of("option1", "option2"));
        List<FieldRow<?>> rows = List.of(TestFieldBuilder.summaryRow, TestFieldBuilder.assigneeLoginNameRow,
                new FieldRow(Optional.of(field), Optional.of(field), "")
        );
        var id = TestUtils.save(getConnector(), task, rows);
        var loadedTask = getConnector().loadTaskByKey(id, rows);
        assertThat(loadedTask.getValue(field)).containsOnly("option1", "option2");
        TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity());
    }

    private static final List<FieldRow<?>> rows = FieldRowBuilder.rows(
            List.of(AllFields.summary, AllFields.taskType));

    @Test
    public void createTaskIsCreatedWithDefaultTaskTypeSetInConfig() throws Exception {
        var created = TestUtils.saveAndLoad(getConnector(),
                new GTaskBuilder().withRandom(AllFields.summary)/*.withField(TaskType, "Story")*/.build(),
                rows);
        assertThat(created.getValue(AllFields.taskType)).isEqualTo(config.getDefaultTaskType());
        TestJiraClientHelper.deleteTasks(client, created.getIdentity());
    }

    @Test
    public void createNewTaskGetsRequestedType() throws Exception {
        var created = TestUtils.saveAndLoad(getConnector(), task("Story"), rows);
        assertThat(created.getValue(AllFields.taskType)).isEqualTo("Story");
        TestJiraClientHelper.deleteTasks(client, created.getIdentity());
    }

    private static GTask task(String taskTypeName) {
        return GTaskBuilder.gtaskWithRandom(AllFields.summary)
                .setValue(AllFields.taskType, taskTypeName);
    }

    @Test
    public void updateDoesNotResetTaskTypeToConfigDefault() {
        var saver = new StatefulTestTaskSaver(getConnector(), JiraPropertiesLoader.getTestServerInfo().getHost());
        // regression test
        var created = saver.saveAndLoad(task("Story"), rows);
        created.setValue(AllFields.taskType, null);
        var updated = saver.saveAndLoad(created, rows);
        assertThat(updated.getValue(AllFields.taskType)).isEqualTo("Story");
        TestJiraClientHelper.deleteTasks(client, created.getIdentity());
    }

    @Test
    public void testGetIssuesByProject() {
        var tasks = generateTasks();
        getConnector().saveData(PreviouslyCreatedTasksResolver.empty,
                tasks, ProgressMonitorUtils.DUMMY_MONITOR, TestFieldBuilder.getSummaryAndAssigneeLogin());
        var jql = JqlBuilder.findIssuesByProject(config.getProjectKey());
        var issues = JiraClientHelper.findIssues(client, jql);
        assertThat(Iterables.size(issues)).isGreaterThan(1);
    }

    private static JiraConnector getConnector() {
        return new JiraConnector(config, JiraPropertiesLoader.getTestServerInfo());
    }

    private static List<GTask> generateTasks() {
        return List.of(GTaskBuilder.withSummary().setId(1L).setKey("1"),
                GTaskBuilder.withSummary().setId(2L).setKey("2"));
    }
}
