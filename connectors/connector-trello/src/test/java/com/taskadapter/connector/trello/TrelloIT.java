package com.taskadapter.connector.trello;

import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTestChecks;
import com.taskadapter.connector.testlib.ITFixture;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import com.taskadapter.test.core.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class TrelloIT {
    private static final WebConnectorSetup setup = TrelloTestConfig.getSetup();
    private static final TrelloClient client = new TrelloClient(setup.getPassword(), setup.getApiKey());

    NewConnector getConnector(String boardId) {
        var config = TrelloTestConfig.getConfig();
        config.setBoardId(boardId);
        return new TrelloConnector(config, setup);
    }

    @Test
    public void taskCreatedAndLoaded() throws IOException {
        withTempBoard(boardId -> {
            // reporter info is not used when creating a task, but will be used to check value in the created tasks
            var task = buildTask().setValue(AllFields.reporterLoginName, "altest6")
                    .setValue(AllFields.reporterFullName, "Alex Skor")
                    .setValue(AllFields.description, "desc");
            var fixture = new ITFixture("Trello server", getConnector(boardId), CommonTestChecks.skipCleanup);
            fixture.taskIsCreatedAndLoaded(task,
                    java.util.Arrays.asList(AllFields.description, AllFields.summary, TrelloField.listName,
                            AllFields.reporterLoginName,
                            AllFields.reporterFullName));
            return null;
        });
    }

    @Test
    public void taskIsCreatedAndUpdated() throws IOException {
        withTempBoard(boardId -> {
            CommonTestChecks.taskCreatedAndUpdatedOK("",
                    getConnector(boardId), TrelloFieldBuilder.getDefault(),
                    buildTask(), AllFields.summary, "new value",
                    CommonTestChecks.skipCleanup);
            return null;
        });
    }

    @Test
    public void properExceptionWithUnknownListName() throws IOException {
        withTempBoard(boardId -> {
            var task = buildTask();
            task.setValue(TrelloField.listName, "unknown list");
            var result = getConnector(boardId).saveData(PreviouslyCreatedTasksResolver.empty,
                    List.of(task),
                    ProgressMonitorUtils.DUMMY_MONITOR, TrelloFieldBuilder.getDefault());
            assertThat(result.getTaskErrors()).hasSize(1);
            var error = result.getTaskErrors().get(0).getError();
            assertThat(error).isInstanceOf(ConnectorException.class)
                    .hasMessageContaining("Trello list with name 'unknown list' is not found on the requested Trello Board");
            return null;
        });
    }

    private GTask buildTask() {
        var task = GTaskBuilder.gtaskWithRandom(AllFields.summary);
        task.setValue(TrelloField.listName, "To Do");
        return task;
    }

    private void withTempBoard(Function<String, Void> function) throws IOException {
        var board = client.createBoard("board-test-" + new Random().nextInt(10000));
        try {
            function.apply(board.getId());
        } finally {
            try {
                client.closeBoard(board.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
