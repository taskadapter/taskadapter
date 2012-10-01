package com.taskadapter.connector.jira;

import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

public class JiraEditorTest {
    @Test
    public void editorInstanceIsCreated() {
        new JiraEditor(new JiraConfig(), new Services(new File("tmp")));
    }
}
