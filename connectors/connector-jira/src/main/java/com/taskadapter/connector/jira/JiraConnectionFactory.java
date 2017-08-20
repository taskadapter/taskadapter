package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

public class JiraConnectionFactory {

    public static JiraRestClient createClient(WebConnectorSetup setup) throws ConnectorException {
        try {
            return createConnection(setup);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    private static JiraRestClient createConnection(WebConnectorSetup setup) throws MalformedURLException, RemoteException, URISyntaxException {
        URI jiraServerUri = new URI(setup.host());
        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, setup.userName(), setup.password());
        return restClient;
    }
}
