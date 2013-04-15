package com.taskadapter.connector.basecamp.classic.transport;


/**
 * Basic http entity. Just an iternal implementation to use with proper
 * wrappers, etc...
 * 
 */
public final class BasicHttpResponse {
    private final int responseCode;
    private final String content;
    private final String location;

    public BasicHttpResponse(int responseCode, String content, String location) {
        super();
        this.responseCode = responseCode;
        this.content = content;
        this.location = location;
    }
    
    public String getLocation() {
        return location;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getContent() {
        return content;
    }
}
