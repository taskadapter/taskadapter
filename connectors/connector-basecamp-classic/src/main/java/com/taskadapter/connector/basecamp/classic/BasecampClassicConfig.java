package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;

public class BasecampClassicConfig extends ConnectorConfig {

    private String projectKey = "";

    private String todoKey = "";

    // saving tasks fails with "HTTP 422" error unless this is set to TRUE.
    // need to either fix this bug or remove the commented out code.
    private boolean lookupUsersByName = true;

    public BasecampClassicConfig() {
        super(new Priorities());
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getTodoKey() {
        return todoKey;
    }

    public void setTodoKey(String todoKey) {
        this.todoKey = todoKey;
    }

    public boolean isLookupUsersByName() {
        return lookupUsersByName;
    }

    public void setLookupUsersByName(boolean lookupUsersByName) {
        this.lookupUsersByName = lookupUsersByName;
    }
}
