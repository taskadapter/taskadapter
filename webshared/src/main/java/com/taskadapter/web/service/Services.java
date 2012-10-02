package com.taskadapter.web.service;

import com.taskadapter.FileManager;
import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;

import java.io.File;

public class Services {
    private Authenticator authenticator;
    private UpdateManager updateManager = new UpdateManager();
    private EditorManager editorManager;
    private PluginManager pluginManager = new PluginManager();
    private SettingsManager settingsManager = new SettingsManager();
    private LicenseManager licenseManager = new LicenseManager();
    private ConfigStorage configStorage;
    private CookiesManager cookiesManager = new CookiesManager();
    private UserManager userManager;
    private FileManager fileManager;

    public Services(File dataRootFolder) {
        userManager = new UserManager(dataRootFolder);
        configStorage = new ConfigStorage(pluginManager, dataRootFolder);
        authenticator = new Authenticator(userManager, cookiesManager);
        fileManager = new FileManager(dataRootFolder);
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public EditorManager getEditorManager() {
        return editorManager;
    }

    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
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

    public UserManager getUserManager() {
        return userManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }
}
