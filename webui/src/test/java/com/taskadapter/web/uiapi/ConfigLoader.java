package com.taskadapter.web.uiapi;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.NewConfigParser;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.webui.service.EditorManager;

import java.io.File;
import java.io.IOException;

public final class ConfigLoader {

    public static UISyncConfig loadConfig(File configFolder, String resourceNameInClassPath) throws IOException {
        String contents = Resources.toString(Resources.getResource(resourceNameInClassPath), Charsets.UTF_8);
        StoredExportConfig config = NewConfigParser.parse("someId", contents);

        EditorManager editorManager = EditorManager.fromResource("editors.txt");
        UIConfigService uiConfigService = new UIConfigService(new PluginManager(), editorManager);
        ConfigStorage storage = new ConfigStorage(configFolder);
        UIConfigStore store = new UIConfigStore(uiConfigService, storage);
        return store.uize("admin", config);
    }

}
