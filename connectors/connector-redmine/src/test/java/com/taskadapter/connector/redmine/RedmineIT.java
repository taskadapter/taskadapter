package com.taskadapter.connector.redmine;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.testlib.TestSaver;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import com.taskadapter.redmineapi.bean.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

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

/*
    @Test
    public void startDateNotExported() throws ConnectorException {
        checkStartDate(getTestSaverWith(RedmineField.startDate()), null);
    }

    @Test
    public void startDateExported() throws ConnectorException {
        checkStartDate(getTestSaverWith(RedmineField.startDate()), TestUtils.getYearAgo());
    }

    @Test
    public void startDateExportedByDefault() throws ConnectorException {
        checkStartDate(getTestSaver(), TestUtils.getYearAgo());
    }

    private TestSaver getTestSaverWith(String field) {
        return getTestSaver(RedmineFieldBuilder.withField(field, ""));
    }
*/
    private TestSaver getTestSaver(List<FieldRow> rows) {
        return new TestSaver(getConnector(), rows);
    }

/*    private TestSaver getTestSaver() {
        return new TestSaver(getConnector(), RedmineFieldBuilder.getDefault());
    }*/

    private TestSaver getTestSaver(RedmineConfig config, List<FieldRow> rows) {
        return new TestSaver(getConnector(config), rows);
    }

/*
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
        GTask loadedTask = getTestSaverWith(RedmineField.dueDate()).saveAndLoad(task);
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
        task.setValue(RedmineField.assignee(), currentUser);
        GTask loadedTask = getTestSaverWith(RedmineField.assignee()).saveAndLoad(task);
        User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee());
        assertEquals(currentUser.getId(), loadedAssignee.getId());
        // only the ID and Display Name are set, so we can't check login name
        assertEquals(currentUser.getDisplayName(), loadedAssignee.getFullName());
    }

    @Test
    public void assigneeNotExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setValue(RedmineField.assignee(), currentUser);
        GTask loadedTask = getTestSaver().saveAndLoad(task);
        User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee());
        assertNull(loadedAssignee);
    }
*/

/*
    @Test
    public void assigneeExportedByDefault() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, RFBTestMappingUtils.fromFields(SUPPORTED_FIELDS));
        User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee());
        assertEquals(currentUser.getId(), loadedAssignee.getId());
    }
*/

    // TODO what does it test?? how is findByName related to Assignee export?
/*
    @Test
    public void assigneeExportedByName() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);

        RedmineConfig config = getTestConfig();
        config.setFindUserByName(true);
        GTask loadedTask = getTestSaver(config, RedmineFieldBuilder.withField(RedmineField.assignee(), "")).saveAndLoad(task);
        User loadedAssignee = (User) loadedTask.getValue(RedmineField.assignee());
        assertEquals(currentUser.getId(), loadedAssignee.getId());
        // only the ID and Display Name are set, so we can't check login name
        assertEquals(currentUser.getDisplayName(), loadedAssignee.getFullName());
    }

*/
/*
    @Test
    public void estimatedTimeNotExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = getTestSaver().unselectField(FIELD.ESTIMATED_TIME).saveAndLoad(task);
        assertNull(loadedTask.getEstimatedHours());
    }


    @Test
    public void estimatedTimeExported() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = getTestSaverWith(RedmineField.estimatedTime()).saveAndLoad(task);
        assertEquals(task.getValue(RedmineField.estimatedTime()), loadedTask.getValue(RedmineField.estimatedTime()));
    }

    @Test
    public void estimatedTimeExportedByDefault() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        assertEquals(task.getValue(RedmineField.estimatedTime()), loadedTask.getValue(RedmineField.estimatedTime()));
    }
*/

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

/*
    @Test
    public void taskStatusNotExported() throws RedmineException, ConnectorException {
        String defaultStatus = getDefaultTaskStatus();

        if (defaultStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setValue(RedmineField.taskStatus(), "Resolved");
            GTask loadedTask = getTestSaver().unselectField(FIELD.TASK_STATUS).saveAndLoad(task);
            assertEquals(defaultStatus, loadedTask.getValue(RedmineField.taskStatus()));
        }
    }
*/

/*
    //temporary ignored (need to add ProjectMemberships to redmine-java-api)
    @Ignore
    @Test
    public void taskStatusExported() throws Exception {
        String otherStatus = getOtherTaskStatus();

        if (otherStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setValue(RedmineField.taskStatus(), otherStatus);
            GTask loadedTask = getTestSaver().selectField(FIELD.TASK_STATUS).saveAndLoad(task);
            assertEquals(otherStatus, loadedTask.getValue(RedmineField.taskStatus()));
        }
    }
*/

/*
    @Test
    public void taskStatusExportedByDefault() throws Exception {
        String defaultStatus = getDefaultTaskStatus();

        if (defaultStatus != null) {
            GTask task = TestUtils.generateTask();
            task.setValue(RedmineField.taskStatus(), null);
            GTask loadedTask = TestUtils.saveAndLoad(getConnector(), task, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
            assertEquals(defaultStatus, loadedTask.getValue(RedmineField.taskStatus()));
        }
    }

    @Test
    public void taskWithChildren() throws Exception {
        GTask t = new GTask();
        t.setId(1);
        String summary = "generic task " + Calendar.getInstance().getTimeInMillis();
        t.setValue(RedmineField.summary(), summary);
        t.setValue(RedmineField.description(), "some descr" + Calendar.getInstance().getTimeInMillis() + "1");
        Random r = new Random();
        int hours = r.nextInt(50) + 1;
        t.setEstimatedHours((float) hours);
        t.setChildren(new ArrayList<>());

        GTask c1 = new GTask();
        c1.setId(3);
        c1.setParentKey("1");
        c1.setValue(RedmineField.summary(), "Child 1 of " + summary);
        t.getChildren().add(c1);

        GTask c2 = new GTask();
        c2.setId(4);
        c2.setParentKey("1");
        c2.setValue(RedmineField.summary(), "Child 2 of " + summary);
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
*/
/*

    @Test
    public void notMappedDescriptionIsSetToEmpty() throws Exception {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = getTestSaver().unselectField(FIELD.DESCRIPTION).saveAndLoad(task);
        assertEquals("", loadedTask.getDescription());
    }
*/

/*
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
        CommonTests.testLoadTasks(getConnector(), RedmineFieldBuilder.getDefault());
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

    private static GTask createTaskWithPrecedesRelations(RedmineConnector redmine, Integer childCount, List<FieldRow> rows) throws ConnectorException {
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
        List<GTask> loadedList = TestUtils.saveAndLoadList(redmine, list, rows);
        return TestUtils.findTaskBySummary(loadedList, task.getSummary());
    }
*/

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