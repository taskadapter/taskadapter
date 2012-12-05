package com.taskadapter.connector.basecamp;

public final class ApiKeyBasecampAuth extends BasecampAuth {
    private String apiKey="";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
