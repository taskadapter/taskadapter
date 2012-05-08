package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.CommonTests;
import com.taskadapter.connector.common.TestSaver;
import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import net.sf.mpxj.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.taskadapter.connector.msp.MSPTestUtils.deleteFile;
import static com.taskadapter.connector.msp.MSPTestUtils.findMSPTaskBySummary;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FieldMappingTest {
    private MSPConfig config;
    private MSPConnector connector;

    @Before
    public void setup() {
        config = new MSPConfig();
        // TODO need to generate a random file here to avoid possible collisions
        // when this test class runs in multiple threads
        config.setInputFileName("msp_test_data.tmp");
        config.setOutputFileName("msp_test_data.tmp");

        connector = new MSPConnector(config);
    }

    @Test
    public void testEstimatedTimeNotSaved() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setEstimatedHours((float) 25);
        List<Task> loadedTasks = saveAndLoad(config, FIELD.ESTIMATED_TIME, new Mapping(false, TaskField.DURATION.toString()), task);
        Task loadedTask = findMSPTaskBySummary(loadedTasks, task.getSummary());

        assertNull(loadedTask.getWork());
        assertNull(loadedTask.getDuration());
    }

    @Test
    public void testEstimatedTimeSaved() throws Exception {
        GTask task = TestUtils.generateTask();
        Float hours = 25f;
        task.setEstimatedHours(hours);
        List<Task> loadedTasks = saveAndLoad(config, FIELD.ESTIMATED_TIME, new Mapping(true, TaskField.DURATION.toString()),
                task);
        Task loadedTask = findMSPTaskBySummary(loadedTasks, task.getSummary());
        assertEquals(hours, loadedTask.getDuration().getDuration(), 0);
    }

    @Test
    public void testEstimatedTimeSavedToWork() throws Exception {
        GTask task = TestUtils.generateTask();
        Float hours = 25f;
        task.setEstimatedHours(hours);
        List<Task> loadedTasks = saveAndLoad(config, FIELD.ESTIMATED_TIME, new Mapping(true, TaskField.WORK.toString()), task);
        Task loadedTask = findMSPTaskBySummary(loadedTasks, task.getSummary());

        assertEquals(hours, loadedTask.getWork().getDuration(), 0);
        assertNull(loadedTask.getDuration());
    }

    @Test
    public void testEstimatedTimeSavedToDuration() throws Exception {
        GTask task = TestUtils.generateTask();
        Float hours = 25f;
        task.setEstimatedHours(hours);
        List<Task> loadedTasks = saveAndLoad(config, FIELD.ESTIMATED_TIME, new Mapping(true, TaskField.DURATION.toString()), task);
        Task loadedTask = findMSPTaskBySummary(loadedTasks, task.getSummary());

        assertEquals(hours, loadedTask.getDuration().getDuration(), 0);
        assertNull(loadedTask.getWork());
    }

    @Test
    public void testDueDateMappedToFinish() throws Exception {
        GTask task = TestUtils.generateTask();
        Date dueDate = getDateRoundedToMinutes();
        task.setDueDate(dueDate);

        List<Task> loadedTasks = saveAndLoad(config, FIELD.DUE_DATE, new Mapping(true, TaskField.FINISH.toString()), task);
        Task loadedTask = findMSPTaskBySummary(loadedTasks, task.getSummary());
        assertEquals(dueDate, loadedTask.getFinish());
        assertEquals(null, loadedTask.getDeadline());
    }

    @Test
    public void testDueDateMappedToDeadline() throws Exception {
        GTask task = TestUtils.generateTask();
        Date dueDate = getDateRoundedToMinutes();
        task.setDueDate(dueDate);

        List<Task> loadedTasks = saveAndLoad(config, FIELD.DUE_DATE, new Mapping(true, TaskField.DEADLINE.toString()), task);
        Task loadedTask = findMSPTaskBySummary(loadedTasks, task.getSummary());
        assertEquals(dueDate, loadedTask.getDeadline());
        assertEquals(null, loadedTask.getFinish());
    }

    @Test
    public void testDueDateNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        Date dueDate = getDateRoundedToMinutes();
        task.setDueDate(dueDate);

        Mapping mapping = new Mapping(false, "some value");
        List<Task> loadedTasks = saveAndLoad(config, FIELD.DUE_DATE, mapping, task);
        Task loadedTask = findMSPTaskBySummary(loadedTasks, task.getSummary());
        assertEquals(null, loadedTask.getDeadline());
        assertEquals(null, loadedTask.getFinish());
    }

    @Test
    public void testDescriptionNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.DESCRIPTION).saveAndLoad(task);
        assertEquals("", loadedTask.getDescription());
    }

    @Test
    public void testDescriptionExported() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.DESCRIPTION).saveAndLoad(task);
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }

    @Test
    public void testDescriptionExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoad(connector, task);
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }

    @Test
    public void testStartDateNotMapped() throws Exception {
        GTask task = TestUtils.generateTask();
        TestUtils.setTaskStartYearAgo(task);
        List<Task> loadedTasks = saveAndLoad(config, FIELD.START_DATE, new Mapping(false, TaskField.DURATION.toString()), task);
        assertNull(loadedTasks.get(0).getStart());
    }

    @Test
    public void testStartDateExportedNoConstraint() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.getDateRoundedToDay();
        yearAgo.add(Calendar.YEAR, -1);
        task.setStartDate(yearAgo.getTime());
        GTask loadedTask = TestUtils.saveAndLoad(connector,
                FIELD.START_DATE, new Mapping(true, MSPAvailableFieldsProvider.NO_CONSTRAINT), task);
        assertEquals(yearAgo.getTime(), loadedTask.getStartDate());
    }

    @Test
    public void testStartDateMustStartOn() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar cal = TestUtils.setTaskStartYearAgo(task);
        GTask loadedTask = TestUtils.saveAndLoad(connector,
                FIELD.START_DATE, new Mapping(true, ConstraintType.MUST_START_ON.name()), task);
        assertEquals(cal.getTime(), loadedTask.getStartDate());
    }

    @Test
    public void testAssigneeNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        GUser assignee = new GUser(123, "some user");
        task.setAssignee(assignee);
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertNull(loadedTask.getAssignee());
    }

    @Test
    public void testAssigneeExported() throws Exception {
        GTask task = TestUtils.generateTask();
        GUser assignee = new GUser(123, "some user");
        task.setAssignee(assignee);
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertEquals(assignee.getId(), loadedTask.getAssignee().getId());
    }

    @Test
    public void testAssigneeExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        GUser assignee = new GUser(123, "some user");
        task.setAssignee(assignee);
        GTask loadedTask = TestUtils.saveAndLoad(connector, task);
        assertEquals(assignee.getId(), loadedTask.getAssignee().getId());
    }

    private static List<Task> saveAndLoad(MSPConfig config, FIELD field, Mapping mapping, GTask... tasks) throws IOException, MPXJException {
        MSPConfig temporaryClonedconfig = new MSPConfig(config);
        temporaryClonedconfig.setFieldMapping(field, mapping);

        String fileName = "testdata.tmp";
        temporaryClonedconfig.setInputFileName(fileName);
        temporaryClonedconfig.setOutputFileName(fileName);
        MSPConnector connector = new MSPConnector(temporaryClonedconfig);

        connector.saveData(Arrays.asList(tasks), null);

        MSPFileReader fileReader = new MSPFileReader();
        ProjectFile projectFile = fileReader.readFile(temporaryClonedconfig.getInputFileName());
        List<Task> loadedTasks = projectFile.getAllTasks();

        deleteFile(fileName);
        return loadedTasks;
    }

    private static Date getDateRoundedToMinutes() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Test
    public void notMappedDescriptionIsSetToEmpty() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.DESCRIPTION).saveAndLoad(task);
        assertEquals("", loadedTask.getDescription());
    }

    @Test
    public void testLoadTasks() throws Exception {
        new CommonTests().testLoadTasks(connector);
    }

    @Test
    public void testDefaultDescriptionMapping() throws Exception {
        new CommonTests().testDefaultDescriptionMapping(connector);
    }

    @Test
    public void descriptionMapped() throws Exception {
        new CommonTests().descriptionMapped(connector);
    }

    @Test
    public void twoTasksAreCreated() throws Exception {
        new CommonTests().testCreates2Tasks(connector);
    }
}
