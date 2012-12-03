package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.basecamp.BasecampAuth;

/**
 * Object API holder.
 * 
 */
abstract class ObjectAPIHolder {
    final ObjectAPI api;
    final String userId;

    public ObjectAPIHolder(ObjectAPI api, String userId) {
        super();
        this.api = api;
        this.userId = userId;
    }

    abstract boolean accepts(BasecampAuth auth);
}
