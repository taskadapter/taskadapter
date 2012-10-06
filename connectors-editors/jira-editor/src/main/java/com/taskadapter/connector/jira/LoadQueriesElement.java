package com.taskadapter.connector.jira;

import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

public class LoadQueriesElement {
    private JiraConfig config;

    public LoadQueriesElement(JiraConfig config) {
        this.config = config;
    }

    List<? extends NamedKeyedObject> loadQueries() {
        try {
            return new JiraConnector(config).getFilters();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
