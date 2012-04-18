package com.taskadapter.webui.service;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.web.SettingsManager;

public class Services {
    private Authenticator authenticator = new Authenticator();
    private UpdateManager updateManager = new UpdateManager();
    private EditorManager editorManager = new EditorManager();
    private PluginManager pluginManager = new PluginManager();
    private ConfigStorage configStorage;
    private SettingsManager settingsManager = new SettingsManager();

    public Services() {
        configStorage = new ConfigStorage(pluginManager);
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public EditorManager getEditorManager() {
        return editorManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public ConfigStorage getConfigStorage() {
        return configStorage;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
}
