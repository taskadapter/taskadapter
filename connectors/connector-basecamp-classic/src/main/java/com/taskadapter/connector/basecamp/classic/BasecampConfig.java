package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;

public class BasecampConfig extends ConnectorConfig {

    private static final String DEFAULT_LABEL = "Basecamp Classic";

    private String apiKey = "";
    
    private String serverUrl = "";

    private String projectKey = "";

    private String todoKey = "";

    // saving tasks fails with "HTTP 422" error unless this is set to TRUE.
    // need to either fix this bug or remove the commented out code.
//    private boolean lookupUsersByName = true;

    public BasecampConfig() {
        super(new Priorities());
        setLabel(DEFAULT_LABEL);
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String key) {
        apiKey = key;
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

//    public boolean isLookupUsersByName() {
//        return lookupUsersByName;
//    }
//
//    public void setLookupUsersByName(boolean lookupUsersByName) {
//        this.lookupUsersByName = lookupUsersByName;
//    }

    public static String getDefaultLabel() {
        return DEFAULT_LABEL;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

}
