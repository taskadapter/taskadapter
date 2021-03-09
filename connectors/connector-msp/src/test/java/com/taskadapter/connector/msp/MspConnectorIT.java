package com.taskadapter.connector.msp;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.TestFieldBuilder;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.testlib.CommonTestChecks;
import com.taskadapter.connector.testlib.FieldRowBuilder;
import com.taskadapter.connector.testlib.ITFixture;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class MspConnectorIT {
    private static final String MSP_FILE_NAME = "msp_test_data.xml";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void connectorIdIsNotChanged() {
        // make sure Connector ID is not changes accidentally. it must stay the same to support loading old configs
        // even if Connector "label" is changed.
        assertThat(MSPConnector.ID).isEqualTo("Microsoft Project");
    }

    @Test
    public void taskIsCreatedAndLoaded() {
        var fixture = new ITFixture("ta-test.tmp", getConnector(tempFolder.getRoot()),
                CommonTestChecks.skipCleanup);
        var task = new GTaskBuilder().withRandom(AllFields.summary)
                .withRandom(MspField.taskDuration)
                .withRandom(MspField.taskWork)
                .withRandom(MspField.mustStartOn)
                .withRandom(MspField.finish)
                .withRandom(MspField.deadline)
                .build()
                .setValue(AllFields.description, "desc")
                .setValue(AllFields.assigneeFullName, "display name")
                .setValue(AllFields.priority, 888);
        fixture.taskIsCreatedAndLoaded(task,
                List.of(AllFields.assigneeFullName, MspField.taskDuration, MspField.taskWork, MspField.mustStartOn, MspField.finish, MspField.deadline,
                        AllFields.description, AllFields.priority, AllFields.summary)
        );
    }

    @Test
    public void estimatedTimeSavedToWork() {
        MSPConnector connector = getConnector(tempFolder.getRoot());
        var task = new GTaskBuilder().withRandom(AllFields.summary)
                .withField(AllFields.estimatedTime, 65F)
                .build();

        List<FieldRow<?>> rows = List.of(
                new FieldRow<>(Optional.of(AllFields.estimatedTime), Optional.of(MspField.taskWork), ""),
                new FieldRow<>(Optional.of(AllFields.summary), Optional.of(AllFields.summary), "")
        );
        GTask createdTask = CommonTestChecks.createAndLoadTask(connector, task, rows);
        assertThat(createdTask.getValue(MspField.taskWork)).isEqualTo(65F);
    }

    @Test
    public void descriptionSavedByDefault() {
        CommonTestChecks.fieldIsSavedByDefault(getConnector(tempFolder.getRoot()),
                new GTaskBuilder()
                        .withRandom(AllFields.summary)
                        .withRandom(AllFields.description)
                        .build(),
                MspField.fields,
                AllFields.description,
                CommonTestChecks.skipCleanup);
    }

    @Test
    public void trimsFieldValueAndRemovesLineBreakAtTheEnd() {
        var textWithEndingLineBreak = " text " + System.lineSeparator();
        var task = new GTask().setValue(AllFields.summary, textWithEndingLineBreak);
        var created = TestUtils.saveAndLoad(getConnector(tempFolder.getRoot()),
                task,
                FieldRowBuilder.rows(
                        List.of(AllFields.summary)
                )
        );
        assertThat(created.getValue(AllFields.summary)).isEqualTo("text");
    }

    @Test
    public void tasksAreLoadedAsTree() {
        var loadedTasks = MSPTestUtils.load("Projeto1.xml");
        assertEquals(3, loadedTasks.size());
        var tree = TreeUtils.buildTreeFromFlatList(loadedTasks);
        assertEquals(1, tree.size());
        assertEquals(2, tree.get(0).getChildren().size());
    }

    @Test
    public void fileCreatedByMSP2013IsLoaded() throws ParseException {
        var tasks = MSPTestUtils.load("msp_2013.xml");
        assertEquals(2, tasks.size());
        var task1 = tasks.get(0);
        assertEquals("task 1", task1.getValue(AllFields.summary));
        assertThat(task1.getValue(AllFields.assigneeFullName)).isEqualTo("alex");
        assertThat(task1.getValue(MspField.taskDuration)).isEqualTo(12f);
        var expectedStartDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/11/2013 08:00");
        assertEquals(expectedStartDate, task1.getValue(MspField.startAsSoonAsPossible));
        var expectedFinishDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse("12/12/2013 12:00");
        assertEquals(expectedFinishDate, task1.getValue(MspField.finish));
    }

    @Test
    public void twoTasksAreCreated() {
        CommonTestChecks.createsTasks(getConnector(tempFolder.getRoot()), TestFieldBuilder.getSummaryRow(),
                java.util.List.of(GTaskBuilder.gtaskWithRandom(AllFields.summary), GTaskBuilder.gtaskWithRandom(AllFields.summary)),
                CommonTestChecks.skipCleanup);
    }

    private static MSPConnector getConnector(File folder) {
        var file = new File(folder, MSP_FILE_NAME);
        var setup = FileSetup.apply(MSPConnector.ID, "label", file.getAbsolutePath(), file.getAbsolutePath());
        return new MSPConnector(setup);
    }
}
