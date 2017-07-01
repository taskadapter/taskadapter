package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.taskadapter.connector.testlib.CommonTests;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.connector.testlib.TestSaver;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Calendar;

import static com.taskadapter.connector.mantis.MantisSupportedFields.SUPPORTED_FIELDS;
import static com.taskadapter.connector.testlib.TestUtils.generateTask;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MantisIT {

    private static final Logger logger = LoggerFactory.getLogger(MantisIT.class);

    private static MantisManager mgr;

    private static String projectKey;
    private MantisConnector mantis;
    private static GUser currentUser;

    @BeforeClass
    public static void oneTimeSetUp() {
        logger.info("Running Mantis BT tests using: " + Config.getURI());
        mgr = new MantisManager(Config.getURI(), Config.getUserLogin(), Config.getPassword());

        ProjectData junitTestProject = new ProjectData();
        junitTestProject.setName("test project" + Calendar.getInstance().getTimeInMillis());
        junitTestProject.setDescription("test" + Calendar.getInstance().getTimeInMillis());

        try {
            AccountData mantisUser = mgr.getCurrentUser();
            currentUser = MantisToGTask.convertToGUser(mantisUser);

            BigInteger projectId = mgr.createProject(junitTestProject);
            projectKey = projectId.toString();
        } catch (Exception e) {
            logger.error("Error loading Mantis BT test properties. " + e.getMessage(), e);
            Assert.fail(e.toString());
        }
    }

    @Before
    public void setUp() throws Exception {
        MantisConfig config = Config.getMantisTestConfig();
        config.setProjectKey(projectKey);
        mantis = new MantisConnector(config);
    }

    @AfterClass
    public static void oneTimeTearDown() {
        try {
            if (mgr != null) {
                mgr.deleteProject(new BigInteger(projectKey));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("can't delete test project with key " + projectKey + ". reason: "
                    + e.getMessage());
        }
    }

    @Test
    public void assigneeExported() throws Exception {
        GTask task = generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = getTestSaver().selectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
    }

    @Test
    public void assigneeNotExported() throws Exception {
        GTask task = generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = getTestSaver().unselectField(FIELD.ASSIGNEE).saveAndLoad(task);
        assertNull(loadedTask.getAssignee());
    }

    @Test
    public void assigneeExportedByDefault() throws Exception {
        GTask task = generateTask();
        task.setAssignee(currentUser);
        GTask loadedTask = getTestSaver().saveAndLoad(task);
        assertEquals(currentUser.getId(), loadedTask.getAssignee().getId());
    }

    @Test
    public void taskUpdatedOK() throws Exception {
        CommonTests.taskCreatedAndUpdatedOK(mantis, SUPPORTED_FIELDS);
    }

    private TestSaver getTestSaver() {
        return new TestSaver(mantis,
                TestMappingUtils
                        .fromFields(SUPPORTED_FIELDS));
    }
}
