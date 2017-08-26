package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;

public class BasecampConfig extends ConnectorConfig {

    private String accountId = "";

    private String projectKey = "";

    private String todoKey = "";

    private Boolean loadCompletedTodos = false;

    private boolean findUserByName;

    public BasecampConfig() {
        super(new Priorities());
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

    public boolean isFindUserByName() {
        return findUserByName;
    }

    public void setFindUserByName(boolean findUserByName) {
        this.findUserByName = findUserByName;
    }

    public Boolean getLoadCompletedTodos() {
        return loadCompletedTodos;
    }

    public void setLoadCompletedTodos(Boolean loadCompletedTodos) {
        this.loadCompletedTodos = loadCompletedTodos;
    }
}
