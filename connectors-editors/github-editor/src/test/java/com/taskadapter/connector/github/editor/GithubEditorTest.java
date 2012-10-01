package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

public class GithubEditorTest {
    @Test
    public void editorInstanceIsCreated() {
        new GithubEditor(new GithubConfig(), new Services(new File("tmp")));
    }
}
