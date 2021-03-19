package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.testlib.CommonTestChecks;
import com.taskadapter.connector.testlib.ITFixture;
import com.taskadapter.connector.testlib.TestSaver;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTaskBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;

public class MantisIT {

    private static Logger logger = LoggerFactory.getLogger(MantisIT.class);
    private static ITFixture fixture;
    private static final WebConnectorSetup setup = MantisTestConfig.getSetup();
    private static MantisManager mgr;
    private static AccountData mantisUser;
    private static String projectKey;
    private static MantisConfig config = new MantisConfig();

    @BeforeClass
    public static void beforeAllTests() throws Exception {
        logger.info("Running Mantis BT tests using: " + setup.getHost());

        mgr = new MantisManager(setup.getHost(), setup.getUserName(), setup.getPassword());
        var junitTestProject = new ProjectData();
        junitTestProject.setName("test project" + Calendar.getInstance().getTimeInMillis());
        junitTestProject.setDescription("test" + Calendar.getInstance().getTimeInMillis());
        mantisUser = mgr.getCurrentUser();
        var projectId = mgr.createProject(junitTestProject);
        projectKey = projectId.toString();
        config.setProjectKey(projectKey);
        var mantisConnector = new MantisConnector(config, setup);

        fixture = new ITFixture(setup.getHost(), mantisConnector, CommonTestChecks.skipCleanup);
    }

    @AfterClass
    public static void afterAllTests() throws RemoteException {
        if (mgr != null) {
            mgr.deleteProject(new BigInteger(projectKey));
        }
    }

    @Test
    public void taskCreatedAndLoaded() {
        fixture.taskIsCreatedAndLoaded(GTaskBuilder.withSummary()
                        .setValue(AllFields.assigneeFullName, mantisUser.getReal_name())
                        .setValue(AllFields.description, "123"),
                List.of(AllFields.assigneeFullName, AllFields.summary, AllFields.description, AllFields.dueDate));
    }

    @Test
    public void taskCreatedAndUpdated() {
        var task = GTaskBuilder.withSummary();
        fixture.taskCreatedAndUpdatedOK(MantisFieldBuilder.getDefault(), task, AllFields.summary, "new value");
    }

    private TestSaver getTestSaver(List<FieldRow<?>> rows) {
        return new TestSaver(getConnector(), rows);
    }

    private MantisConnector getConnector() {
        return getConnector(config);
    }

    private MantisConnector getConnector(MantisConfig config) {
        return new MantisConnector(config, setup);
    }

}
