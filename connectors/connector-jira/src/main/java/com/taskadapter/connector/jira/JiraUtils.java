package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;
import com.taskadapter.connector.jira.exceptions.BadHostException;
import com.taskadapter.connector.jira.exceptions.BadURIException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Collection;

public final class JiraUtils {

    public static ConnectorException convertException(RemoteException e) {
        return new CommunicationException(e.toString(), e);
    }

    public static ConnectorException convertException(Exception e) {
        if (e instanceof RestClientException) {
            return processRestException((RestClientException) e);
        }
        return new ConnectorException(e);
    }

    private static ConnectorException processRestException(RestClientException e) {
        if (e.getStatusCode().isPresent() && e.getStatusCode().get().equals(401)) {
            return new NotAuthorizedException();
        }

        String errorMessage = "";
        final Collection<ErrorCollection> errorCollections = e.getErrorCollections();
        for (ErrorCollection collection : errorCollections) {
            for (String s : collection.getErrorMessages()) {
                errorMessage += s + System.lineSeparator();
            }
        }
        return new ConnectorException(errorMessage);

    }

    public static ConnectorException convertException(MalformedURLException e) {
        return new BadHostException(e);
    }

    public static ConnectorException convertException(URISyntaxException e) {
        return new BadURIException(e);
    }

    public static String getIdFromURI(URI self) {
        if (self != null) {
            String path = self.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        }
        return null;
    }

}
