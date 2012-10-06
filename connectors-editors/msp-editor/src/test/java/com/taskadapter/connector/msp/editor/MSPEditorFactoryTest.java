package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Authenticator;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MSPEditorFactoryTest {
    @Test
    public void miniPanelInstanceIsCreated() {
        Services services = new Services(new File("testfolder.tmp"));
        Authenticator authenticator = mock(Authenticator.class);
        when(authenticator.getUserName()).thenReturn("admin");
        services.setAuthenticator(authenticator);
        MSPEditorFactory factory = new MSPEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider, services, new MSPConfig());
    }

}
