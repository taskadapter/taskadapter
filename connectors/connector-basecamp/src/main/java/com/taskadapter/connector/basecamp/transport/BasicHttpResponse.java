package com.taskadapter.connector.basecamp.transport;


/**
 * Basic http entity. Just an iternal implementation to use with proper
 * wrappers, etc...
 * 
 */
public final class BasicHttpResponse {
    private final int responseCode;
    private final String content;

    public BasicHttpResponse(int responseCode, String content) {
        super();
        this.responseCode = responseCode;
        this.content = content;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getContent() {
        return content;
    }
}
