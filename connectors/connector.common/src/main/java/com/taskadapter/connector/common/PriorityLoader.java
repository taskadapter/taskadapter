package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.NamedKeyedObjectImpl;

import java.util.List;

public interface PriorityLoader {
    List<NamedKeyedObjectImpl> getPriorities(WebServerInfo serverInfo) throws ValidationException;
}
