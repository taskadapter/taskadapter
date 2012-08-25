package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.atlassian.jira_soapclient.SOAPSession;
import com.taskadapter.connector.definition.WebServerInfo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;

public class JiraConnectionFactory {
    public static JiraConnection createConnection(WebServerInfo info) throws MalformedURLException, RemoteException, URISyntaxException {
        JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
        URI jiraServerUri = new URI(info.getHost());
        JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, info.getUserName(), info.getPassword());

        String baseUrl = info.getHost() + "/rpc/soap/jirasoapservice-v2";
        SOAPSession soapSession = new SOAPSession(new URL(baseUrl));
        soapSession.connect(info.getUserName(), info.getPassword());
        return new JiraConnection(soapSession.getJiraSoapService(), soapSession.getAuthenticationToken(), restClient);
    }
}
