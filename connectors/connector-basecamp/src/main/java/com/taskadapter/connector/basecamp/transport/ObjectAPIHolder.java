package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.definition.WebConnectorSetup;

abstract class ObjectAPIHolder {
    final ObjectAPI api;
    final String userId;

    ObjectAPIHolder(ObjectAPI api, String userId) {
        super();
        this.api = api;
        this.userId = userId;
    }

    abstract boolean accepts(WebConnectorSetup setup);
}
