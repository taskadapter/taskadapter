package com.taskadapter.web.service;

import com.taskadapter.FileManager;
import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UIConfigStore;

import java.io.File;

public class Services {
    private final EditorManager editorManager;
    private PluginManager pluginManager = new PluginManager();
    private SettingsManager settingsManager = new SettingsManager();
    private LicenseManager licenseManager;
    private CookiesManager cookiesManager = new CookiesManager();
    private final EditableCurrentUserInfo currentUserInfo = new EditableCurrentUserInfo();
    private UserManager userManager;
    private FileManager fileManager;
    private UIConfigStore uiConfigStore;

    // TODO this is not the right place for this variable.
    private String currentTaskAdapterVersion;

    public Services(File dataRootFolder, EditorManager editorManager) {
        this.editorManager = editorManager;
        fileManager = new FileManager(dataRootFolder);
        userManager = new UserManager(fileManager);
        final ConfigStorage configStorage = new ConfigStorage(fileManager);

        this.uiConfigStore = new UIConfigStore(new UIConfigService(
                pluginManager, editorManager), configStorage);

        this.currentTaskAdapterVersion = new CurrentVersionLoader().getCurrentVersion();

        licenseManager = new LicenseManager(dataRootFolder);
    }

    public EditorManager getEditorManager() {
        return editorManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
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

    public UIConfigStore getUIConfigStore() {
        return uiConfigStore;
    }
    
    public CurrentUserInfo getCurrentUserInfo() {
        return currentUserInfo;
    }

    public String getCurrentTaskAdapterVersion() {
        return currentTaskAdapterVersion;
    }
}
