package com.taskadapter.connector.basecamp.classic.transport;

/**
 * Object API holder.
 * 
 */
final class ObjectAPIHolder {
    final String apiUrl;
    final String authKey;
    final ObjectAPI api;

    public ObjectAPIHolder(String apiUrl, String authKey, ObjectAPI api) {
        this.apiUrl = apiUrl;
        this.authKey = authKey;
        this.api = api;
    }

}
