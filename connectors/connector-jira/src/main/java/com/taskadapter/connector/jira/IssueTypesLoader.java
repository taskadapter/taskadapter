package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IssueTypesLoader {

    static List<NamedKeyedObject> getIssueTypes(JiraConfig config, IssueTypeFilter issueTypesFilter) throws ConnectorException {
        JiraConfigValidator.validateServerURLSet(config);
        try(JiraRestClient client = JiraConnectionFactory.createClient(config.getServerInfo())) {
            Promise<Iterable<IssueType>> issueTypeListPromise = client.getMetadataClient().getIssueTypes();
            final Iterable<IssueType> issueTypes = issueTypeListPromise.claim();
            Iterable<IssueType> filtered = issueTypesFilter.filter(issueTypes);
            List<NamedKeyedObject> list = new ArrayList<>();

            for (IssueType type : filtered) {
                list.add(new NamedKeyedObjectImpl(String.valueOf(type.getId()), type.getName()));
            }
            return list;
        } catch (IOException e) {
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
        List<IssueType> issueTypesForSubtasks = new ArrayList<>();
        for (IssueType issueType : types) {
            if (issueType.isSubtask()) {
                issueTypesForSubtasks.add(issueType);
            }
        }
        return issueTypesForSubtasks;
    }
}
