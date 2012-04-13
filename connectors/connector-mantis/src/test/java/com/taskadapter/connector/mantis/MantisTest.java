package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mantis.ta.MantisManager;
import org.mantis.ta.beans.AccountData;
import org.mantis.ta.beans.ProjectData;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.taskadapter.connector.common.TestUtils.generateTask;
import static com.taskadapter.connector.common.TestUtils.saveAndLoad;

public class MantisTest {

    private static MantisManager mgr;

    private static String projectKey;
    private static MantisConfig config = Config.getMantisTestConfig();
    private MantisConnector mantis = new MantisConnector(config);
    private static GUser currentUser;

    private static Map<GTaskDescriptor.FIELD, Mapping> defaultFieldsMap;

    @BeforeClass
    public static void oneTimeSetUp() {
        System.out.println("Running mantis tests using: " + Config.getURI());
        mgr = new MantisManager(Config.getURI(), Config.getUserLogin(), Config.getPassword());

        ProjectData junitTestProject = new ProjectData();
        junitTestProject.setName("test project" + Calendar.getInstance().getTimeInMillis());
        junitTestProject.setDescription("test" + Calendar.getInstance().getTimeInMillis());

        defaultFieldsMap = new HashMap<GTaskDescriptor.FIELD, Mapping>();
        defaultFieldsMap.put(GTaskDescriptor.FIELD.SUMMARY, new Mapping());
        defaultFieldsMap.put(GTaskDescriptor.FIELD.DESCRIPTION, new Mapping());
        defaultFieldsMap.put(GTaskDescriptor.FIELD.ASSIGNEE, new Mapping());
        //defaultFieldsMap.put(GTaskDescriptor.FIELD.DUE_DATE, new Mapping());

        try {
            AccountData mantisUser = mgr.getCurrentUser();
            currentUser = MantisDataConverter.convertToGUser(mantisUser);

            BigInteger projectId = mgr.createProject(junitTestProject);
            projectKey = projectId.toString();
            config.setProjectKey(projectKey);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        try {
            if (mgr != null) {
                mgr.deleteProject(new BigInteger(projectKey));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("can't delete the test project '" + projectKey + ". reason: "
                    + e.getMessage());
        }
    }

/*    @Test
    public void testStartDateNotExported() throws Exception {
        GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
        Calendar today = getDateRoundedToDay();
        TestUtils.setTaskStartYearAgo(task);
        GTask loadedTask = TestUtils.saveAndLoad(mantis, GTaskDescriptor.FIELD.START_DATE, new Mapping(false), task);
        Assert.assertEquals(today.getTime(), loadedTask.getStartDate());
    }

	@Test
	public void testStartDateExported() throws Exception {
		GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
		Calendar yearAgo = TestUtils.setTaskStartYearAgo(task);
		GTask loadedTask = TestUtils.saveAndLoad(mantis, GTaskDescriptor.FIELD.START_DATE, new Mapping(true), task);
		Assert.assertEquals(yearAgo.getTime(), loadedTask.getStartDate());
	}

	@Test
	public void testStartDateExportedByDefault() throws Exception {
		GTask task = TestUtils.generateTask();
        task.setAssignee(currentUser);
		Calendar yearAgo = TestUtils.setTaskStartYearAgo(task);
		GTask loadedTask = TestUtils.saveAndLoad(mantis, task);
		Assert.assertEquals(yearAgo.getTime(), loadedTask.getStartDate());
	}*/

/*	@Test
	public void testDueDateNotExported() throws Exception {
		GTask task = generateTask();
        task.setAssignee(currentUser);
		setTaskDueDateNextYear(task);

        Map<GTaskDescriptor.FIELD, Mapping> tempMap = new HashMap<GTaskDescriptor.FIELD, Mapping>();
        tempMap.put(GTaskDescriptor.FIELD.SUMMARY, new Mapping());
        tempMap.put(GTaskDescriptor.FIELD.DESCRIPTION, new Mapping());
        tempMap.put(GTaskDescriptor.FIELD.DUE_DATE, new Mapping(false));

		GTask loadedTask = saveAndLoad(mantis, tempMap, task);
		Assert.assertNull(loadedTask.getDueDate());
	}

	@Test
	public void testDueDateExported() throws Exception {
		GTask task = generateTask();
        task.setAssignee(currentUser);
		Calendar yearAgo = setTaskDueDateNextYear(task);
		GTask loadedTask = saveAndLoad(mantis, defaultFieldsMap, task);
		Assert.assertEquals(yearAgo.getTime(), loadedTask.getDueDate());
	}

	@Test
	public void testDueDateExportedByDefault() throws Exception {
		GTask task = generateTask();
        task.setAssignee(currentUser);
		Calendar yearAgo = setTaskDueDateNextYear(task);
		GTask loadedTask = saveAndLoad(mantis, task);
		Assert.assertEquals(yearAgo.getTime(), loadedTask.getDueDate());
	}*/

    @Test
    public void testAssigneeExported() throws Exception {
        GTask task = generateTask();
        task.setAssignee(currentUser);
        testAssignee(defaultFieldsMap, task, currentUser.getId());
    }

    @Test
    public void testAssigneeNotExported() throws Exception {
        GTask task = generateTask();
        task.setAssignee(currentUser);

        Map<GTaskDescriptor.FIELD, Mapping> tempMap = new HashMap<GTaskDescriptor.FIELD, Mapping>();
        tempMap.put(GTaskDescriptor.FIELD.SUMMARY, new Mapping());
        tempMap.put(GTaskDescriptor.FIELD.DESCRIPTION, new Mapping());
        tempMap.put(GTaskDescriptor.FIELD.ASSIGNEE, new Mapping(false));

        GTask loadedTask = saveAndLoad(mantis, tempMap, task);
        Assert.assertNull(loadedTask.getAssignee());
    }

    @Test
    public void testAssigneeExportedByDefault() throws Exception {
        GTask task = generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = saveAndLoad(mantis, defaultFieldsMap, task);
        Assert.assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
    }

    public void testAssignee(Map<GTaskDescriptor.FIELD, Mapping> map, GTask task, Integer id) throws Exception {
        GTask loadedTask = saveAndLoad(mantis, map, task);
        Assert.assertEquals(id, loadedTask.getAssignee().getId());
    }

/*	@Test
	public void testEstimatedTimeNotExported() throws Exception {
		GTask task = TestUtils.generateTask();
		GTask loadedTask = TestUtils.saveAndLoad(mantis, TestUtils.getFieldMapped(GTaskDescriptor.FIELD.ESTIMATED_TIME, false), task);
		Assert.assertEquals(0, loadedTask.getEstimatedHours(), 0);
	}

	@Test
	public void testEstimatedTimeExported() throws Exception {
		GTask task = TestUtils.generateTask();
		GTask loadedTask = TestUtils.saveAndLoad(mantis, TestUtils.getFieldMapped(GTaskDescriptor.FIELD.ESTIMATED_TIME, true), task);
		Assert.assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
	}

	@Test
	public void testEstimatedTimeExportedByDefault() throws Exception {
		GTask task = TestUtils.generateTask();
		GTask loadedTask = TestUtils.saveAndLoad(mantis, task);
		Assert.assertEquals(task.getEstimatedHours(), loadedTask.getEstimatedHours(), 0);
	}

	@Test
	public void testTaskWithChildren() throws Exception {
		GTask t = new GTask();
		t.setId(1);
		String summary = "generic task " + Calendar.getInstance().getTimeInMillis();
		t.setSummary(summary);
		t.setDescription("some descr" + Calendar.getInstance().getTimeInMillis()+ "1");
		Random r = new Random();
		int hours = r.nextInt(50)+1;
		t.setEstimatedHours((float) hours);
		t.setChildren(new ArrayList<GTask>());

		GTask c1 = new GTask();
		c1.setId(3);
		c1.setParentId(1);
		c1.setSummary("Child 1 of " + summary);
		t.getChildren().add(c1);

		GTask c2 = new GTask();
		c2.setId(4);
		c2.setParentId(1);
		c2.setSummary("Child 2 of " + summary);
		t.getChildren().add(c2);
		GTask task = t;
		List<GTask> loadedTasks = TestUtils.saveAndLoadAll(mantis, task);

		for (Iterator<GTask> iterator = loadedTasks.iterator(); iterator.hasNext();) {
			GTask gTask = iterator.next();
			if (! gTask.getSummary().endsWith(summary)) iterator.remove();
		}

		List<GTask> tree = TreeUtils.buildTreeFromFlatList(loadedTasks);

		Assert.assertEquals(1, tree.size());

		GTask parent = tree.get(0);

		Assert.assertEquals(2, parent.getChildren().size());
	}*/

    // not used now but should be used in AllConnectorsTest
    public ConnectorConfig getTestConfig() {
        return config;
    }
}
