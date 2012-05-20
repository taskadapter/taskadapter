package com.taskadapter.web;

import com.taskadapter.model.GTaskDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class MessagesTest {
    @Test
    public void taskTypeMessageNotEmpty() throws Exception {
        assertFalse(Messages.getMessageDefaultLocale(GTaskDescriptor.FIELD.TASK_TYPE.toString()).isEmpty());
    }

    @Test
    public void taskStatusMessageNotEmpty() throws Exception {
        assertFalse(Messages.getMessageDefaultLocale(GTaskDescriptor.FIELD.TASK_STATUS.toString()).isEmpty());
    }

    @Test
    public void remoteIdMessageNotEmpty() throws Exception {
        assertFalse(Messages.getMessageDefaultLocale(GTaskDescriptor.FIELD.REMOTE_ID.toString()).isEmpty());
    }
}
