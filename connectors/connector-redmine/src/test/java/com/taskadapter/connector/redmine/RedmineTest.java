package com.taskadapter.connector.redmine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.taskadapter.connector.common.TestSaver;
import com.taskadapter.connector.definition.WebServerInfo;
import org.junit.*;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.IssueStatus;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.User;

import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import com.taskadapter.connector.common.CommonTests;
import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.common.TreeUtils;

/**
 * Integration tests for Redmine Connector.
 *
 * @author Alexey Skorokhodov
 */
public class RedmineTest {

    private static RedmineManager mgr;

    private static String projectKey;
    private RedmineConfig config;
    private RedmineConnector connector;
    private static GUser currentUser;

    @BeforeClass
    public static void oneTimeSetUp() {
        WebServerInfo serverInfo = RedmineTestConfig.getRedmineTestConfig().getServerInfo();
        System.out.println("Running redmine tests using: " + serverInfo);
        mgr = new RedmineManager(serverInfo.getHost(), serverInfo.getUserName(), serverInfo.getPassword());

        Project junitTestProject = new Project();
        junitTestProject.setName("zmd test project");
        junitTestProject.setIdentifier("test"
                + Calendar.getInstance().getTimeInMillis());
        try {
            User redmineUser = mgr.getCurrentUser();
            currentUser = RedmineDataConverter.convertToGUser(redmineUser);

            Project createdProject = mgr.createProject(junitTestProject);
            projectKey = createdProject.getIdentifier();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setUp() throws Exception {
        config = RedmineTestConfig.getRedmineTestConfig();
        config.setProjectKey(projectKey);
        connector = new RedmineConnector(config);
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
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.START_DATE).saveAndLoad(task);
        assertNull(loadedTask.getStartDate());
    }

    @Test
    public void startDateExported() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskStartYearAgo(task);
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.START_DATE).saveAndLoad(task);
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
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.DUE_DATE).saveAndLoad(task);
        assertNull(loadedTask.getDueDate());
    }

    @Test
    public void dueDateExported() throws Exception {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task);
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.DUE_DATE).saveAndLoad(task);
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
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
        // only the ID and Display Name are set, so we can't check login name
        assertEquals(currentUser.getDisplayName(), loadedTask.getAssignee().getDisplayName());
    }

    @Test
    public void assigneeNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertNull(loadedTask.getAssignee());
    }

    @Test
    public void assigneeExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = TestUtils.saveAndLoad(connector, task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
    }

    // TODO what does it test?? how is findByName related to Assignee export?
    @Test
    public void assigneeExportedByName() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        config.setFindUserByName(true);
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
        // only the ID and Display Name are set, so we can't check login name
        assertEquals(currentUser.getDisplayName(), loadedTask.getAssignee().getDisplayName());
    }

    @Test
    public void estimatedTimeNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.ESTIMATED_TIME).saveAndLoad(task);
        assertNull(loadedTask.getEstimatedHours());
    }

    @Test
    public void estimatedTimeExported() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.ESTIMATED_TIME).saveAndLoad(task);
        assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
    }

    @Test
    public void estimatedTimeExportedByDefault() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoad(connector, task);
        assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
    }

    public String getDefaultTaskStatus() throws Exception {
        String statusName = null;

        List<IssueStatus> list = mgr.getStatuses();
        for (IssueStatus status : list) {
            if (status.isDefaultStatus()) {
                statusName = status.getName();
                break;
            }
        }

        return statusName;
    }

    public String getOtherTaskStatus() throws Exception {
        String statusName = null;

        List<IssueStatus> list = mgr.getStatuses();
        for (IssueStatus status : list) {
            if (!status.isDefaultStatus()) {
                statusName = status.getName();
                break;
            }
        }

        return statusName;
    }

    @Test
    public void taskStatusNotExported() throws Exception {
        String defaultStatus = getDefaultTaskStatus();

        if (defaultStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setStatus("Resolved");
            GTask loadedTask = new TestSaver(connector).unselectField(FIELD.TASK_STATUS).saveAndLoad(task);
            assertEquals(defaultStatus, loadedTask.getStatus());
        }
    }

    //temporary ignored (need to add ProjectMemberships to redmine-java-api
    @Ignore
    @Test
    public void taskStatusExported() throws Exception {
        String otherStatus = getOtherTaskStatus();

        if (otherStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setStatus(otherStatus);
            GTask loadedTask = new TestSaver(connector).selectField(FIELD.TASK_STATUS).saveAndLoad(task);
            assertEquals(otherStatus, loadedTask.getStatus());
        }
    }

    @Test
    public void taskStatusExportedByDefault() throws Exception {
        String defaultStatus = getDefaultTaskStatus();

        if (defaultStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setStatus(null);
            GTask loadedTask = TestUtils.saveAndLoad(connector, task);
            assertEquals(defaultStatus, loadedTask.getStatus());
        }
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
        GTask loadedTask = new TestSaver(connector).unselectField(FIELD.DESCRIPTION).saveAndLoad(task);
        assertEquals("", loadedTask.getDescription());
    }

    @Test
    public void taskUpdateTaskWithDeletedRelation() throws Exception {
        connector.getConfig().setSaveIssueRelations(true);
        GTask loadedTask = RedmineUtils.generateTaskWithPrecedesRelations(connector, 2);

        ArrayList<GTask> taskList = new ArrayList<GTask>(3);
        loadedTask.setRemoteId(loadedTask.getKey());
        taskList.add(loadedTask);

        GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey());
        task.setRemoteId(task.getKey());
        taskList.add(task);

        task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey());
        task.setRemoteId(task.getKey());
        taskList.add(task);

        loadedTask.getRelations().remove(0);
        TestUtils.saveAndLoadList(connector, taskList);
        GTask newTask = connector.loadTaskByKey(loadedTask.getKey());

        assertEquals(1, newTask.getRelations().size());
    }

    @Test
    public void taskUpdateTaskWithCreatedRelation() throws Exception {
        connector.getConfig().setSaveIssueRelations(true);
        GTask loadedTask = RedmineUtils.generateTaskWithPrecedesRelations(connector, 2);

        ArrayList<GTask> taskList = new ArrayList<GTask>(3);
        loadedTask.setRemoteId(loadedTask.getKey());
        taskList.add(loadedTask);

        GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey());
        task.setRemoteId(task.getKey());
        taskList.add(task);

        task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey());
        task.setRemoteId(task.getKey());
        taskList.add(task);

        GTask t = TestUtils.generateTask();
        GTask newTask = TestUtils.saveAndLoad(connector, t);
        newTask.setRemoteId(newTask.getKey());
        taskList.add(newTask);

        loadedTask.getRelations().add(new GRelation(loadedTask.getRemoteId(), newTask.getKey(), GRelation.TYPE.precedes));
        TestUtils.saveAndLoadList(connector, taskList);
        newTask = connector.loadTaskByKey(loadedTask.getKey());

        assertEquals(3, newTask.getRelations().size());
    }


    @Test
    public void someTasksAreLoaded() throws Exception {
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
