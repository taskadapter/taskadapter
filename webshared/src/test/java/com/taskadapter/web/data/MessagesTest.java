package com.taskadapter.web.data;

import com.taskadapter.model.GTaskDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class MessagesTest {
    private static final String BUNDLE_NAME = "help";
    private static final com.taskadapter.web.data.Messages MESSAGES = new com.taskadapter.web.data.Messages(BUNDLE_NAME);

    @Test
    public void missingKeyIsNullWhenUsingGetNoDefault() {
        assertNull(MESSAGES.getNoDefault("non-existing-key"));
    }

    @Test
    public void missingKeyIsReturnedWithSomeDecorationWhenUsingGet() {
        assertEquals("!non-existing-key!", MESSAGES.get("non-existing-key"));
    }

    @Test
    public void taskTypeMessageNotEmpty() {
        assertFalse(MESSAGES.get(GTaskDescriptor.FIELD.TASK_TYPE.toString()).isEmpty());
    }

    @Test
    public void taskStatusMessageNotEmpty() {
        assertFalse(MESSAGES.get(GTaskDescriptor.FIELD.TASK_STATUS.toString()).isEmpty());
    }

    @Test
    public void remoteIdMessageNotEmpty() {
        assertFalse(MESSAGES.get(GTaskDescriptor.FIELD.REMOTE_ID.toString()).isEmpty());
    }
}
