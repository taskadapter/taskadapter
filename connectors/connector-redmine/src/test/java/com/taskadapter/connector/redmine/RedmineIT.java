package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTests;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.connector.testlib.TestSaver;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import com.taskadapter.redmineapi.bean.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.taskadapter.connector.redmine.RedmineSupportedFields.SUPPORTED_FIELDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class RedmineIT {

    private static final Logger logger = LoggerFactory.getLogger(RedmineIT.class);

    private static RedmineManager mgr;

    private static String projectKey;
    private static GUser currentUser;

    @BeforeClass
    public static void oneTimeSetUp() {
        WebServerInfo serverInfo = RedmineTestConfig.getRedmineTestConfig().getServerInfo();
        logger.info("Running Redmine tests with: " + serverInfo);
        try {
            mgr = RedmineManagerFactory.createRedmineManager(serverInfo);

            Project junitTestProject = ProjectFactory.create("TA Redmine Integration test project",
                    "test" + Calendar.getInstance().getTimeInMillis());
            User redmineUser = mgr.getUserManager().getCurrentUser();
            currentUser = RedmineToGUser.convertToGUser(redmineUser);

            Project createdProject = mgr.getProjectManager().createProject(junitTestProject);
            logger.info("Created temporary Redmine project with ID " + junitTestProject.getIdentifier());
            projectKey = createdProject.getIdentifier();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        try {
            if (mgr != null) {
                mgr.getProjectManager().deleteProject(projectKey);
                logger.info("Deleted temporary Redmine project with ID " + projectKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("can't delete the test project '" + projectKey + ". reason: "
                    + e.getMessage());
        }
    }

    @Test
    public void startDateNotExported() throws ConnectorException {
        checkStartDate(getTestSaver().unselectField(FIELD.START_DATE), null);
    }

    @Test
    public void startDateExported() throws ConnectorException {
        checkStartDate(getTestSaver().selectField(FIELD.START_DATE), TestUtils.getYearAgo());
    }

    @Test
    public void startDateExportedByDefault() throws ConnectorException {
        checkStartDate(getTestSaver(), TestUtils.getYearAgo());
    }

    private TestSaver getTestSaver() {
        return new TestSaver(getConnector(), TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    private TestSaver getTestSaver(RedmineConfig config) {
        return new TestSaver(getConnector(config), TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    private void checkStartDate(TestSaver testSaver, Date expected) throws ConnectorException {
        GTask task = TestUtils.generateTask();
        Date yearAgo = TestUtils.getYearAgo();
        task.setStartDate(yearAgo);
        GTask loadedTask = testSaver.saveAndLoad(task);
        assertEquals(expected, loadedTask.getStartDate());
    }

    @Test
    public void dueDateNotExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        TestUtils.setTaskDueDateNextYear(task);
        GTask loadedTask = getTestSaver().unselectField(FIELD.DUE_DATE).saveAndLoad(task);
        assertNull(loadedTask.getDueDate());
    }

    @Test
    public void dueDateExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task);
        GTask loadedTask = getTestSaver().selectField(FIELD.DUE_DATE).saveAndLoad(task);
        assertEquals(yearAgo.getTime(), loadedTask.getDueDate());
    }

    @Test
    public void dueDateExportedByDefault() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        Calendar yearAgo = TestUtils.setTaskDueDateNextYear(task);
        GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        assertEquals(yearAgo.getTime(), loadedTask.getDueDate());
    }

    @Test
    public void assigneeExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = getTestSaver().selectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
        // only the ID and Display Name are set, so we can't check login name
        assertEquals(currentUser.getDisplayName(), loadedTask.getAssignee().getDisplayName());
    }

    @Test
    public void assigneeNotExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = getTestSaver().unselectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertNull(loadedTask.getAssignee());
    }

    @Test
    public void assigneeExportedByDefault() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
    }

    // TODO what does it test?? how is findByName related to Assignee export?
    @Test
    public void assigneeExportedByName() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);

        RedmineConfig config = getTestConfig();
        config.setFindUserByName(true);
        GTask loadedTask = getTestSaver(config).selectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
        // only the ID and Display Name are set, so we can't check login name
        assertEquals(currentUser.getDisplayName(), loadedTask.getAssignee().getDisplayName());
    }

    @Test
    public void estimatedTimeNotExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = getTestSaver().unselectField(FIELD.ESTIMATED_TIME).saveAndLoad(task);
        assertNull(loadedTask.getEstimatedHours());
    }

    @Test
    public void estimatedTimeExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = getTestSaver().selectField(FIELD.ESTIMATED_TIME).saveAndLoad(task);
        assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
    }

    @Test
    public void estimatedTimeExportedByDefault() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
    }

    private String getDefaultTaskStatus() throws RedmineException {
        String statusName = null;

        List<IssueStatus> list = mgr.getIssueManager().getStatuses();
        for (IssueStatus status : list) {
            if (status.isDefaultStatus()) {
                statusName = status.getName();
                break;
            }
        }

        return statusName;
    }

    private String getOtherTaskStatus() throws RedmineException {
        String statusName = null;

        List<IssueStatus> list = mgr.getIssueManager().getStatuses();
        for (IssueStatus status : list) {
            if (!status.isDefaultStatus()) {
                statusName = status.getName();
                break;
            }
        }

        return statusName;
    }

    @Test
    public void taskStatusNotExported() throws RedmineException, ConnectorException {
        String defaultStatus = getDefaultTaskStatus();

        if (defaultStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setStatus("Resolved");
            GTask loadedTask = getTestSaver().unselectField(FIELD.TASK_STATUS).saveAndLoad(task);
            assertEquals(defaultStatus, loadedTask.getStatus());
        }
    }

    //temporary ignored (need to add ProjectMemberships to redmine-java-api)
    @Ignore
    @Test
    public void taskStatusExported() throws Exception {
        String otherStatus = getOtherTaskStatus();

        if (otherStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setStatus(otherStatus);
            GTask loadedTask = getTestSaver().selectField(FIELD.TASK_STATUS).saveAndLoad(task);
            assertEquals(otherStatus, loadedTask.getStatus());
        }
    }

    @Test
    public void taskStatusExportedByDefault() throws Exception {
        String defaultStatus = getDefaultTaskStatus();

        if (defaultStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setStatus(null);
            GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
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
        t.setChildren(new ArrayList<>());

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

        List<GTask> loadedTasks = TestUtils.saveAndLoadAll(getConnector(), t, TestMappingUtils.fromFields(SUPPORTED_FIELDS));

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
        RedmineConfig config = getTestConfig();
        config.setSaveIssueRelations(false);
        GTask loadedTask = createTaskWithPrecedesRelations(getConnector(config), 2, TestMappingUtils.fromFields(SUPPORTED_FIELDS));

        assertEquals(0, loadedTask.getRelations().size());
    }

    @Test
    public void taskExportedWithRelations() throws Exception {
        RedmineConfig config = getTestConfig();
        config.setSaveIssueRelations(true);
        GTask loadedTask = createTaskWithPrecedesRelations(getConnector(config), 2, TestMappingUtils.fromFields(SUPPORTED_FIELDS));

        assertEquals(2, loadedTask.getRelations().size());
    }

    @Test
    public void notMappedDescriptionIsSetToEmpty() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = getTestSaver().unselectField(FIELD.DESCRIPTION).saveAndLoad(task);
        assertEquals("", loadedTask.getDescription());
    }

    @Test
    public void taskUpdateTaskWithDeletedRelation() throws Exception {
        RedmineConfig config = getTestConfig();
        config.setSaveIssueRelations(true);
        Mappings mapping = TestMappingUtils.fromFields(SUPPORTED_FIELDS);
        RedmineConnector connector = getConnector(config);
        GTask loadedTask = createTaskWithPrecedesRelations(connector, 2, mapping);

        ArrayList<GTask> taskList = new ArrayList<>(3);
        loadedTask.setRemoteId(loadedTask.getKey());
        taskList.add(loadedTask);

        GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey(), mapping);
        task.setRemoteId(task.getKey());
        taskList.add(task);

        task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey(), mapping);
        task.setRemoteId(task.getKey());
        taskList.add(task);

        loadedTask.getRelations().remove(0);
        TestUtils.saveAndLoadList(connector, taskList, mapping);
        GTask newTask = connector.loadTaskByKey(loadedTask.getKey(), mapping);

        assertEquals(1, newTask.getRelations().size());
    }

    @Test
    public void taskUpdateTaskWithCreatedRelation() throws Exception {
        RedmineConfig config = getTestConfig();
        config.setSaveIssueRelations(true);
        Mappings mapping = TestMappingUtils.fromFields(SUPPORTED_FIELDS);
        RedmineConnector connector = getConnector(config);
        GTask loadedTask = createTaskWithPrecedesRelations(connector, 2, mapping);

        ArrayList<GTask> taskList = new ArrayList<>(3);
        loadedTask.setRemoteId(loadedTask.getKey());
        taskList.add(loadedTask);

        GTask task = connector.loadTaskByKey(loadedTask.getRelations().get(0).getRelatedTaskKey(), mapping);
        task.setRemoteId(task.getKey());
        taskList.add(task);

        task = connector.loadTaskByKey(loadedTask.getRelations().get(1).getRelatedTaskKey(), mapping);
        task.setRemoteId(task.getKey());
        taskList.add(task);

        GTask t = TestUtils.generateTask();
        GTask newTask = TestUtils.saveAndLoad(connector, t, mapping);
        newTask.setRemoteId(newTask.getKey());
        taskList.add(newTask);

        loadedTask.getRelations().add(new GRelation(loadedTask.getRemoteId(), newTask.getKey(), GRelation.TYPE.precedes));
        TestUtils.saveAndLoadList(connector, taskList, mapping);
        newTask = connector.loadTaskByKey(loadedTask.getKey(), mapping);

        assertEquals(3, newTask.getRelations().size());
    }


    @Test
    public void someTasksAreLoaded() throws Exception {
        CommonTests.testLoadTasks(getConnector(), TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void defaultDescriptionIsMapped() throws Exception {
        CommonTests.descriptionSavedByDefault(getConnector(), TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void descriptionSavedIfSelected() throws Exception {
        CommonTests.descriptionSavedIfSelected(getConnector(), TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void twoTasksAreCreated() throws Exception {
        CommonTests.testCreates2Tasks(getConnector(), TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void taskUpdatedOK() throws Exception {
        CommonTests.taskCreatedAndUpdatedOK(getConnector(), SUPPORTED_FIELDS);
    }

    private static GTask createTaskWithPrecedesRelations(RedmineConnector redmine, Integer childCount, Mappings mapping) throws ConnectorException {
        List<GTask> list = new ArrayList<>();

        GTask task = TestUtils.generateTask();
        task.setId(1);
        list.add(task);

        for (int i = 0; i < childCount; i++) {
            GTask task1 = TestUtils.generateTask();
            task1.setId(i + 2);

            task.getRelations().add(new GRelation(task.getId().toString(), task1.getId().toString(), GRelation.TYPE.precedes));
            list.add(task1);
        }
        List<GTask> loadedList = TestUtils.saveAndLoadList(redmine, list, mapping);
        return TestUtils.findTaskBySummary(loadedList, task.getSummary());
    }

    private RedmineConfig getTestConfig() {
        RedmineConfig config = RedmineTestConfig.getRedmineTestConfig();
        config.setProjectKey(projectKey);
        return config;
    }

    private RedmineConnector getConnector() {
        return getConnector(getTestConfig());
    }

    private RedmineConnector getConnector(RedmineConfig config) {
        return new RedmineConnector(config);
    }

}
