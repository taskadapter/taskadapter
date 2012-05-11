package com.taskadapter.web.service;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;

public class Services {
    private Authenticator authenticator = new Authenticator();
    private UpdateManager updateManager = new UpdateManager();
    private EditorManager editorManager = new EditorManager();
    private PluginManager pluginManager = new PluginManager();
    private SettingsManager settingsManager = new SettingsManager();
    private LicenseManager licenseManager = new LicenseManager();
    private ConfigStorage configStorage;
    private CookiesManager cookiesManager = new CookiesManager();

    public Services() {
        configStorage = new ConfigStorage(pluginManager);
        // TODO remove before the release
        //authenticator.tryLogin("admin", "");
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

    public LicenseManager getLicenseManager() {
        return licenseManager;
    }

    public CookiesManager getCookiesManager() {
        return cookiesManager;
    }
}
