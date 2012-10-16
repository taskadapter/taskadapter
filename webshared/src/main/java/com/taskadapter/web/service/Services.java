package com.taskadapter.web.service;

import com.taskadapter.FileManager;
import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.connector.github.GithubConnector;
import com.taskadapter.connector.jira.JiraConnector;
import com.taskadapter.connector.mantis.MantisConnector;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UIConfigStore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Services {
    // TODO !!! "webshared" module is a library for all editors. it should not have hardcoded editors list.
    private final static Map<String, String> EDITORS = new HashMap<String, String>();
    static {
        EDITORS.put(RedmineConnector.ID, "com.taskadapter.connector.redmine.editor.RedmineEditorFactory");
        EDITORS.put(JiraConnector.ID, "com.taskadapter.connector.jira.JiraEditorFactory");
        EDITORS.put(MSPConnector.ID, "com.taskadapter.connector.msp.editor.MSPEditorFactory");
        EDITORS.put(GithubConnector.ID, "com.taskadapter.connector.github.editor.GithubEditorFactory");
        EDITORS.put(MantisConnector.ID, "com.taskadapter.connector.mantis.editor.MantisEditorFactory");
    }

    private Authenticator authenticator;
    private UpdateManager updateManager = new UpdateManager();
    private final EditorManager editorManager;
    private PluginManager pluginManager = new PluginManager();
    private SettingsManager settingsManager = new SettingsManager();
    private LicenseManager licenseManager = new LicenseManager();
    private CookiesManager cookiesManager = new CookiesManager();
    private UserManager userManager;
    private FileManager fileManager;
    private UIConfigStore uiConfigStore;

    public Services(File dataRootFolder, EditorManager editorManager) {
        this.editorManager = editorManager;
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
