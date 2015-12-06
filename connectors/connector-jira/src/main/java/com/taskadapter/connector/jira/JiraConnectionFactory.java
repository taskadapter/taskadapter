package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

public class JiraConnectionFactory {

    public static JiraRestClient createClient(WebServerInfo info) throws ConnectorException {
        try {
            return createConnection(info);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }

    private static JiraRestClient createConnection(WebServerInfo info) throws MalformedURLException, RemoteException, URISyntaxException {
        URI jiraServerUri = new URI(info.getHost());
        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, info.getUserName(), info.getPassword());
        return restClient;
    }
}
