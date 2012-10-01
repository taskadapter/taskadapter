package com.taskadapter.connector.jira;

import com.taskadapter.web.service.Services;
import org.junit.Test;

public class JiraEditorTest {
    @Test
    public void editorInstanceIsCreated() {
        new JiraEditor(new JiraConfig(), new Services());
    }
}
