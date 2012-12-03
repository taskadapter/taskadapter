package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;

public class BasecampEditorFactory {
    private final ObjectAPIFactory factory = new ObjectAPIFactory(
            new BaseCommunicator());
}
