package com.taskadapter.connector.msp;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import org.junit.Before;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class MSPToGTaskTest {

    private ProjectFile projectFile = MSPTestUtils.readTestProjectFile();

    private static final String FEATURE = "new feature";
    private static final String DEV_TASK = "dev task";
    private static final String BUG = "bug";

    private static final String REMOTE_ID1 = "remoteId-1";
    private static final String REMOTE_ID2 = "remoteId-2";
    private static final String REMOTE_ID3 = "remoteId-3";

    private Task task1;
    private Task task2;
    private Task task3;

    @Before
    public void setUp() throws Exception {
        List<Task> allTasks = projectFile.getAllTasks();
        task1 = allTasks.get(2);
        task2 = allTasks.get(3);
        task3 = allTasks.get(4);
    }

    // TODO TA3 Msp tests
/*
    @Test
    public void extractAssignee1() {
        MSPToGTask converter = getConverter();
        GUser assignee = converter.extractAssignee(task1);
        assertEquals("Assignee ID must be NULL because this file was created by an old Task Adapter version",
                null, assignee.getId());
        assertEquals("wax", converter.extractAssignee(task1).getDisplayName());
    }

    @Test
    public void extractAssignee2() {
        MSPToGTask converter = getConverter();
        GUser assignee = converter.extractAssignee(task1);
        assertEquals("Assignee ID must be NULL because this file was created by an old Task Adapter version",
                null, assignee.getId());
        assertEquals("Alex", converter.extractAssignee(task2).getDisplayName());
    }

    @Test
    public void extractAssignee3() {
        MSPToGTask converter = getConverter();
        GUser assignee = converter.extractAssignee(task1);
        assertEquals("Assignee ID must be NULL because this file was created by an old Task Adapter version",
                null, assignee.getId());
        assertEquals("im", converter.extractAssignee(task3).getDisplayName());
    }
*/

/*
    @Test
    public void estimatedTimeFoundThroughWork() throws BadConfigException {
        Mappings mappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        mappings.setMapping(FIELD.ESTIMATED_TIME, true, TaskField.WORK.toString(), "default time");
        assertEquals(Float.valueOf(2), getConverter(mappings).extractEstimatedHours(task1));
    }

    @Test
    public void estimatedTimeFoundThroughDuration() throws BadConfigException {
        Mappings mappings = TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS);
        mappings.setMapping(FIELD.ESTIMATED_TIME, true, TaskField.DURATION.toString(), "default duration");
        Assert.assertEquals(0.5f, getConverter(mappings).extractEstimatedHours(task2), 0.001f);
    }

    @Test
    public void estimatedTimeFoundWithDefaultMapping() throws BadConfigException {
        assertEquals(8.0f, getConverter().extractEstimatedHours(task3), 0.001f);
    }

    @Test
    public void extractRemoteId() {
        MSPToGTask converter = getConverter();
        assertThat(converter.extractField(task1, SOURCE_SYSTEM_ID)).isEqualTo(REMOTE_ID1);
        assertThat(converter.extractField(task2, SOURCE_SYSTEM_ID)).isEqualTo(REMOTE_ID2);
        assertThat(converter.extractField(task3, SOURCE_SYSTEM_ID)).isEqualTo(REMOTE_ID3);
    }

    @Test
    public void taskTypeIsExtracted() {
        MSPToGTask converter = getConverter();
        assertThat(converter.extractField(task1, TASK_TYPE)).isEqualTo(FEATURE);
        assertThat(converter.extractField(task2, TASK_TYPE)).isEqualTo(DEV_TASK);
        assertThat(converter.extractField(task3, TASK_TYPE)).isEqualTo(BUG);
    }

    private MSPToGTask getConverter() {
        return getConverter(TestMappingUtils.fromFields(MSPSupportedFields.SUPPORTED_FIELDS));
    }

    private MSPToGTask getConverter(Mappings mappings) {
        MSPToGTask converter = new MSPToGTask(mappings);
        converter.setHeader(projectFile.getProjectHeader());
        return converter;
    }
*/
}
