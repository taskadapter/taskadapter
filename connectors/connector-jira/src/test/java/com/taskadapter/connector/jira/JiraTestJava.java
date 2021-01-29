package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.ITFixture;
import com.taskadapter.connector.testlib.TestUtilsJava;
import com.taskadapter.core.JavaFieldAdapter;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Description$;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import com.taskadapter.model.TaskType$;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.taskadapter.core.JavaFieldAdapter.descriptionOpt;
import static com.taskadapter.core.JavaFieldAdapter.summaryOpt;
import static org.assertj.core.api.Assertions.assertThat;

public class JiraTestJava {
    private static Logger logger = LoggerFactory.getLogger(JiraTestJava.class);

    private static JiraConfig config = JiraPropertiesLoader.createTestConfig();
    private static WebConnectorSetup setup = JiraPropertiesLoader.getTestServerInfo();
    private static JiraRestClient client;
    private static JiraConnector connector;
    private static ITFixture fixture;

    @BeforeClass
    public static void beforeAllTests() throws ConnectorException {
        client = JiraConnectionFactory.createClient(setup);
        connector = new JiraConnector(config, setup);
        logger.info("Running JIRA tests using: " + setup.host());
        fixture = new ITFixture(setup.host(), connector, id -> {
            TestJiraClientHelper.deleteTasks(client, id);
            return null;
        });
    }

    @AfterClass
    public static void afterAllTests() throws IOException {
        client.close();
    }

    // TODO move to some generic tests, this is not Jira-specific
    @Test
    public void taskCreatedWithDefaultDescriptionField() {
        // description is empty so that the default value will be set later
        GTask task = GTaskBuilder.withSummary(new Random().nextDouble() + "");

        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(descriptionOpt, descriptionOpt, "some default")
        );
        SaveResult result = connector.saveData(PreviouslyCreatedTasksResolver.empty(), Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertThat(result.createdTasksNumber()).isEqualTo(1);
        TaskId taskId = result.keyToRemoteKeyList().head()._2;
        GTask loadedTask = connector.loadTaskByKey(taskId, rows);
        assertThat(loadedTask.getValue(Description$.MODULE$))
                .isEqualTo("some default");
        TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity());
    }

    @Test
    public void epicWithNameDefinedViaCustomField() {
        CustomString epicName = JavaFieldAdapter.customString("Epic Name");
        List<FieldRow<?>> rows = JavaFieldAdapter.rows(JavaFieldAdapter.summary,
                JavaFieldAdapter.taskType,
                epicName);

        GTask created = TestUtilsJava.saveAndLoad(connector,
                task("Epic").setValue(epicName, "some epic"),
                rows);
        assertThat(created.getValue(TaskType$.MODULE$)).isEqualTo("Epic");
        TestJiraClientHelper.deleteTasks(client, created.getIdentity());
    }

    private static GTask task(String taskTypeName) {
        return GTaskBuilder.withRandomJava(JavaFieldAdapter.summary).setValue(TaskType$.MODULE$, taskTypeName);
    }

    private JiraConnector getConnector() {
        return new JiraConnector(config, JiraPropertiesLoader.getTestServerInfo());
    }
}
