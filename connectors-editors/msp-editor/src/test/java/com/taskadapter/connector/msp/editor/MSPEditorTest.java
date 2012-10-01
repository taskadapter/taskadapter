package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

public class MSPEditorTest {
    @Test
    public void editorInstanceIsCreated() {
        new MSPEditor(new MSPConfig(), new Services(new File("testfolder.tmp")));
    }
}
