package com.taskadapter.webui.service;

import com.taskadapter.FileManager;
import com.taskadapter.PluginManager;
import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.webui.config.ConfigAccessorProvider;

public class Services {
    private final EditorManager editorManager;
    private PluginManager pluginManager = new PluginManager();
    private SettingsManager settingsManager = new SettingsManager();
    private LicenseManager licenseManager;
    private final EditableCurrentUserInfo currentUserInfo = new EditableCurrentUserInfo();
    private FileManager fileManager;
    private UIConfigStore uiConfigStore;

    // TODO this is not the right place for this variable.
    private String currentTaskAdapterVersion;

    /** Configuration accessors. */
    private final ConfigAccessorProvider configAccessor;

    public Services(FileManager fileManager, EditorManager editorManager,
            CredentialsManager credentialManager) {
        this.editorManager = editorManager;
        this.fileManager = fileManager;
        final ConfigStorage configStorage = new ConfigStorage(fileManager);

        this.uiConfigStore = new UIConfigStore(new UIConfigService(
                pluginManager, editorManager), configStorage);
        
        this.currentTaskAdapterVersion = new CurrentVersionLoader()
                .getCurrentVersion();
        
        this.configAccessor = new ConfigAccessorProvider(currentUserInfo,
                getAuthorizedOperations(), uiConfigStore, credentialManager);

        licenseManager = new LicenseManager(fileManager.getLicenseDir());

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
    
    public ConfigAccessorProvider getConfigAccessor() {
        return configAccessor;
    }
    
    /** 
     * Returns list of authorized operations for current user.
     */
    public AuthorizedOperations getAuthorizedOperations() {
        return new DefaultAutorizedOps(currentUserInfo, settingsManager);
    }

    // TODO: Should it live here or somewhere else?
    public Sandbox createCurrentUserSandbox() {
        return new Sandbox(settingsManager.isTAWorkingOnLocalMachine(),
                fileManager.getUserFilesFolder(currentUserInfo.getUserName()));
    }
}
