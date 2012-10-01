package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

public class RedmineEditorTest {
    @Test
    public void editorInstanceIsCreated() {
        new RedmineEditor(new RedmineConfig(), new Services(new File("tmp")));
    }
}
