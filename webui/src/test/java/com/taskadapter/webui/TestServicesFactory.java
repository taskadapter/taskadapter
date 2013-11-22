package com.taskadapter.webui;

import com.taskadapter.FileManager;
import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.FSCredentialStore;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Services;

import java.io.File;
import java.util.Collections;

public class TestServicesFactory {
    public static Services createServices(File folder) {
        final FileManager fileManager = new FileManager(folder);
        final CredentialsStore store = new FSCredentialStore(fileManager);
        final CredentialsManager credentialManager = new BasicCredentialsManager(store, 50);
        return new Services(fileManager, new EditorManager(Collections.<String, PluginEditorFactory<?>>emptyMap()),
                credentialManager);
    }
}
