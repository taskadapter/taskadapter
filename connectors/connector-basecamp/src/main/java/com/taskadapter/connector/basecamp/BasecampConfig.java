package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;

public class BasecampConfig extends ConnectorConfig {

    private static final String DEFAULT_LABEL = "Basecamp";

    private BasecampAuth auth = new BasicBasecampAuth();

    private String accountId = "";

    private String projectKey = "";

    private String todoKey = "";

    private boolean lookupUsersByName;

    public BasecampConfig() {
        super(new Priorities());
        setLabel(DEFAULT_LABEL);
    }

    public BasecampAuth getAuth() {
        return auth;
    }

    public void setAuth(BasecampAuth auth) {
        this.auth = auth;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public static String getDefaultLabel() {
        return DEFAULT_LABEL;
    }

}
