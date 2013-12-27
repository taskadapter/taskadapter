package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.IssueType;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class IssueTypesLoader {

    static List<NamedKeyedObject> getIssueTypes(JiraConfig config, IssueTypeFilter issueTypesFilter) throws ConnectorException {
        JiraConfigValidator.validateServerURLSet(config);
        try {
            JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
            Iterable<IssueType> issueTypeList = connection.getIssueTypeList();
            Iterable<IssueType> filtered = issueTypesFilter.filter(issueTypeList);
            List<NamedKeyedObject> list = new ArrayList<NamedKeyedObject>();

            for (IssueType type : filtered) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(type.getId()), type.getName()));
            }
            return list;
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }
    }
}

abstract class IssueTypeFilter {
    abstract Iterable<IssueType> filter(Iterable<IssueType> types);
}

class AllIssueTypesFilter extends IssueTypeFilter {
    @Override
    Iterable<IssueType> filter(Iterable<IssueType> types) {
        return types;
    }
}

class SubtaskTypesFilter extends IssueTypeFilter {
    @Override
    Iterable<IssueType> filter(Iterable<IssueType> types) {
        List<IssueType> issueTypesForSubtasks = new ArrayList<IssueType>();
        for (IssueType issueType : types) {
            if (issueType.isSubtask()) {
                issueTypesForSubtasks.add(issueType);
            }
        }
        return issueTypesForSubtasks;
    }
}
