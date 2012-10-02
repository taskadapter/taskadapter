package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.service.Authenticator;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MSPEditorTest {
    @Test
    public void editorInstanceIsCreated() {
        Services services = new Services(new File("testfolder.tmp"));
        Authenticator authenticator = mock(Authenticator.class);
        when(authenticator.getUserName()).thenReturn("admin");
        services.setAuthenticator(authenticator);
        new MSPEditor(new MSPConfig(), services);
    }
}
