package com.taskadapter.web.uiapi;

import com.taskadapter.PluginManager;
import com.taskadapter.auth.cred.FSCredentialStore;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.webui.service.EditorManager;

import java.io.File;

public class TestUIConfigStoreFactory {
    public static UIConfigStore createStore(File temporaryFolder) {
        var configStorage = new ConfigStorage(temporaryFolder);
        var editorManager = EditorManager.fromResource("editors.txt");
        var uiConfigService = new UIConfigService(new PluginManager(), editorManager);
        return new UIConfigStore(uiConfigService, configStorage, new FSCredentialStore(temporaryFolder));
    }

}
