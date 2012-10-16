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
    // TODO !!! "webshared" module is a library for all editors. it should not have hardcoded editors list.
    private final static String EDITORS =
            "com.taskadapter.connector.redmine.editor.RedmineEditorFactory\n" +
                    "com.taskadapter.connector.jira.JiraEditorFactory\n" +
                    "com.taskadapter.connector.msp.editor.MSPEditorFactory\n" +
                    "com.taskadapter.connector.github.editor.GithubEditorFactory\n" +
                    "com.taskadapter.connector.mantis.editor.MantisEditorFactory";

    private Authenticator authenticator;
    private UpdateManager updateManager = new UpdateManager();
    private EditorManager editorManager = new EditorManager(EDITORS);
    private PluginManager pluginManager = new PluginManager();
    private SettingsManager settingsManager = new SettingsManager();
    private LicenseManager licenseManager = new LicenseManager();
    private CookiesManager cookiesManager = new CookiesManager();
    private UserManager userManager;
    private FileManager fileManager;
    private UIConfigStore uiConfigStore;

    public Services(File dataRootFolder) {
        fileManager = new FileManager(dataRootFolder);
        userManager = new UserManager(fileManager);
        final ConfigStorage configStorage = new ConfigStorage(fileManager);
        authenticator = new Authenticator(userManager, cookiesManager);

        this.uiConfigStore = new UIConfigStore(new UIConfigService(
                pluginManager, editorManager), configStorage);
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

    public UIConfigStore getUIConfigStore() {
        return uiConfigStore;
    }

    @Deprecated
    public void setAuthenticator(Authenticator authenticator2) {
        this.authenticator = authenticator2;
    }

}
