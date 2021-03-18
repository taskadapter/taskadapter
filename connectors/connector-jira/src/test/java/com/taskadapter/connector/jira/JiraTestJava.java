package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.ITFixture;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.core.JavaFieldAdapter;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
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
        logger.info("Running JIRA tests using: " + setup.getHost());
        fixture = new ITFixture(setup.getHost(), connector, id -> {
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
    public void taskCreatedWithDefaultDescriptionField() throws Exception {
        // description is empty so that the default value will be set later
        GTask task = GTaskBuilder.withSummary(new Random().nextDouble() + "");

        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(descriptionOpt, descriptionOpt, "some default")
        );
        SaveResult result = connector.saveData(PreviouslyCreatedTasksResolver.empty, Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertThat(result.getCreatedTasksNumber()).isEqualTo(1);
        TaskId taskId = result.getKeyToRemoteKeyList().get(0).newId;
        GTask loadedTask = connector.loadTaskByKey(taskId, rows);
        assertThat(loadedTask.getValue(AllFields.description))
                .isEqualTo("some default");
        TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity());
    }

    @Test
    public void epicWithNameDefinedViaCustomField() throws Exception {
        CustomString epicName = JavaFieldAdapter.customString("Epic Name");
        List<FieldRow<?>> rows = JavaFieldAdapter.rows(AllFields.summary,
                AllFields.taskType,
                epicName);

        GTask created = TestUtils.saveAndLoad(connector,
                task("Epic").setValue(epicName, "some epic"),
                rows);
        assertThat(created.getValue(AllFields.taskType)).isEqualTo("Epic");
        TestJiraClientHelper.deleteTasks(client, created.getIdentity());
    }

    private static GTask task(String taskTypeName) {
        return GTaskBuilder.gtaskWithRandomJava(AllFields.summary).setValue(AllFields.taskType, taskTypeName);
    }

    private JiraConnector getConnector() {
        return new JiraConnector(config, JiraPropertiesLoader.getTestServerInfo());
    }
}
