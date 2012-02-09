package com.taskadapter.integrationtests;

import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * This class runs the SAME set of tests for several connectors (which are listed in "data()" method below).
 * This is to make sure that these connectors have some basic functionality working without duplicating those tests in every one of them.
 *
 * @author Alexey Skorokhodov
 */
@RunWith(LabeledParameterized.class)
public class AllConnectorsTest {

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"MSPConnector", new MSPConnector(new MSPConfig("mspxmlfile.tmp"))}
                , {"RedmineConnector", new RedmineConnector(RedmineTestConfig.getRedmineTestConfig()),}
                // TODO bring these connectors back!
//				, { "JiraConnector", new JiraConnector(new JiraTest().getTestConfig() )}
//				, { "GithubConnector", new GithubConnector(new GithubTest().getTestConfig() )}

                // See bug http://www.hostedredmine.com/issues/39264
//				, { "MantisConnector", new MantisConnector(new MantisTest().getTestConfig() ), new MantisTester()	}
        });
    }

    private final Connector connector;

    static Set<String> initializedConnectors = new HashSet<String>();

    public AllConnectorsTest(String label, Connector connector) throws Exception {
        this.connector = connector;
    }

    @Test
    public void testConnectorDoesNotFailWithNULLMonitorAndEmptyList()
            throws Exception {
        connector.saveData(new ArrayList<GTask>(), null);
    }

    @Test
    public void allSupportedFieldsReportedAsAvailable() {
        AvailableFieldsProvider availableFieldsProvider = connector.getDescriptor().getAvailableFieldsProvider();
        Collection<FIELD> supportedFields = availableFieldsProvider.getSupportedFields();
        for (FIELD field : supportedFields) {
            String[] allowedValues = availableFieldsProvider.getAllowedValues(field);
            // fields must have some "textual" value if reported as "supported" by the provider
            assertTrue(allowedValues.length > 0);
        }
    }
}
