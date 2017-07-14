package com.taskadapter.web.uiapi;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.core.TaskKeeper;
import com.taskadapter.webui.service.EditorManager;

import java.io.File;

final class TestUIConfigStoreFactory {
    static UIConfigStore createStore(File temporaryFolder) {
        final ConfigStorage configStorage = new ConfigStorage(temporaryFolder);

        EditorManager editorManager = EditorManager.fromResource("editors.txt");
        UIConfigService uiConfigService = new UIConfigService(new PluginManager(), editorManager);
        TaskKeeper taskKeeper = new TaskKeeper(temporaryFolder);
        return new UIConfigStore(taskKeeper, uiConfigService, configStorage);
    }

}
