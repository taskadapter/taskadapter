package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.CommonTests;
import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.User;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Integration tests for Redmine Connector.
 *
 * @author Alexey Skorokhodov
 */
public class RedmineTest {

    private static RedmineManager mgr;

    private static String projectKey;
    private static RedmineConfig config = RedmineTestConfig.getRedmineTestConfig();
    private RedmineConnector connector = new RedmineConnector(config);
    private static GUser currentUser;

    @BeforeClass
    public static void oneTimeSetUp() {
        System.out.println("Running redmine tests using: " + RedmineTestConfig.getURI());
        mgr = new RedmineManager(RedmineTestConfig.getURI(), RedmineTestConfig.getUserLogin(), RedmineTestConfig.getPassword());

        Project junitTestProject = new Project();
        junitTestProject.setName("test project");
        junitTestProject.setIdentifier("test"
                + Calendar.getInstance().getTimeInMillis());
        try {
            User redmineUser = mgr.getCurrentUser();
            currentUser = RedmineDataConverter.convertToGUser(redmineUser);

            Project createdProject = mgr.createProject(junitTestProject);
            projectKey = createdProject.getIdentifier();
            config.setProjectKey(projectKey);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        try {
            if (mgr != null) {
                mgr.deleteProject(projectKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("can't delete the test project '" + projectKey + ". reason: "
                    + e.getMessage());
        }
    }

    @Test
    public void startDateNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        TestUtils.setTaskStartYearAgo(task);
        GTask loadedTask = TestUtils.saveAndLoad(connector, FIELD.START_DATE, new Mapping(false), task);
        assertNull(loadedTask.getStartDate());
    }

    @Test
    public void startDateExported() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskStartYearAgo(task);
        GTask loadedTask = TestUtils.saveAndLoad(connector, FIELD.START_DATE, new Mapping(true), task);
        assertEquals(yearAgo.getTime(), loadedTask.getStartDate());
    }

    @Test
    public void startDateExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskStartYearAgo(task);
        GTask loadedTask = TestUtils.saveAndLoad(connector, task);
        assertEquals(yearAgo.getTime(), loadedTask.getStartDate());
    }

    @Test
    public void dueDateNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        TestUtils.setTaskDueDateNextYear(task);
        Map<GTaskDescriptor.FIELD, Mapping> mapping = config.generateDefaultFieldsMapping();
        mapping.put(FIELD.DUE_DATE, new Mapping(false));

        GTask loadedTask = TestUtils.saveAndLoad(connector, mapping, task);
        assertNull(loadedTask.getDueDate());
    }

    @Test
    public void dueDateExported() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task);
        Map<GTaskDescriptor.FIELD, Mapping> mapping = config.generateDefaultFieldsMapping();
        mapping.put(FIELD.DUE_DATE, new Mapping(true));

        GTask loadedTask = TestUtils.saveAndLoad(connector, mapping, task);
        assertEquals(yearAgo.getTime(), loadedTask.getDueDate());
    }

    @Test
    public void dueDateExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task);
        GTask loadedTask = TestUtils.saveAndLoad(connector, task);
        assertEquals(yearAgo.getTime(), loadedTask.getDueDate());
    }

    @Test
    public void assigneeExported() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        Map<GTaskDescriptor.FIELD, Mapping> mapping = config.generateDefaultFieldsMapping();
        mapping.put(FIELD.ASSIGNEE, new Mapping(true));
        GTask loadedTask = TestUtils.saveAndLoad(connector, mapping, task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
        // only the ID and Display Name are set, so we can't check login name
        assertEquals(currentUser.getDisplayName(), loadedTask.getAssignee().getDisplayName());
    }

    @Test
    public void assigneeNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        Map<GTaskDescriptor.FIELD, Mapping> mapping = config.generateDefaultFieldsMapping();
        mapping.put(FIELD.ASSIGNEE, new Mapping(false));
        GTask loadedTask = TestUtils.saveAndLoad(connector, mapping, task);
        assertNull(loadedTask.getAssignee());
    }

    @Test
    public void assigneeExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        Map<GTaskDescriptor.FIELD, Mapping> mapping = config.generateDefaultFieldsMapping();
        mapping.put(FIELD.ASSIGNEE, new Mapping(true));

        GTask loadedTask = TestUtils.saveAndLoad(connector, mapping, task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
    }

    @Test
    public void estimatedTimeNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        Map<GTaskDescriptor.FIELD, Mapping> mapping = config.generateDefaultFieldsMapping();
        mapping.put(FIELD.ESTIMATED_TIME, new Mapping(false));

        GTask loadedTask = TestUtils.saveAndLoad(connector, mapping, task);
        assertNull(loadedTask.getEstimatedHours());
    }

    @Test
    public void estimatedTimeExported() throws Exception {
        GTask task = TestUtils.generateTask();
        Map<GTaskDescriptor.FIELD, Mapping> mapping = config.generateDefaultFieldsMapping();
        mapping.put(FIELD.ESTIMATED_TIME, new Mapping(true));

        GTask loadedTask = TestUtils.saveAndLoad(connector, mapping, task);
        assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
    }

    @Test
    public void estimatedTimeExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoad(connector, task);
        assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
    }

    @Test
    public void taskWithChildren() throws Exception {
        GTask t = new GTask();
        t.setId(1);
        String summary = "generic task " + Calendar.getInstance().getTimeInMillis();
        t.setSummary(summary);
        t.setDescription("some descr" + Calendar.getInstance().getTimeInMillis() + "1");
        Random r = new Random();
        int hours = r.nextInt(50) + 1;
        t.setEstimatedHours((float) hours);
        t.setChildren(new ArrayList<GTask>());

        GTask c1 = new GTask();
        c1.setId(3);
        c1.setParentKey("1");
        c1.setSummary("Child 1 of " + summary);
        t.getChildren().add(c1);

        GTask c2 = new GTask();
        c2.setId(4);
        c2.setParentKey("1");
        c2.setSummary("Child 2 of " + summary);
        t.getChildren().add(c2);

        List<GTask> loadedTasks = TestUtils.saveAndLoadAll(connector, t);

        for (Iterator<GTask> iterator = loadedTasks.iterator(); iterator.hasNext(); ) {
            GTask gTask = iterator.next();
            if (!gTask.getSummary().endsWith(summary)) iterator.remove();
        }

        List<GTask> tree = TreeUtils.buildTreeFromFlatList(loadedTasks);

        assertEquals(1, tree.size());

        GTask parent = tree.get(0);

        assertEquals(2, parent.getChildren().size());
    }

    @Test
    public void taskExportedWithoutRelations() throws Exception {
        connector.getConfig().setSaveIssueRelations(false);
        GTask loadedTask = RedmineUtils.generateTaskWithPrecedesRelations(connector, 2);

        assertEquals(0, loadedTask.getRelations().size());
    }

    @Test
    public void taskExportedWithRelations() throws Exception {
        connector.getConfig().setSaveIssueRelations(true);
        GTask loadedTask = RedmineUtils.generateTaskWithPrecedesRelations(connector, 2);

        assertEquals(2, loadedTask.getRelations().size());
    }

    @Test
    public void notMappedDescriptionIsSetToEmpty() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoad(connector, FIELD.DESCRIPTION, new Mapping(false), task);
        assertEquals("", loadedTask.getDescription());
    }

    @Test
    public void someTasksAreLoader() throws Exception {
        new CommonTests().testLoadTasks(connector);
    }

    @Test
    public void defaultDescriptionIsMapped() throws Exception {
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
