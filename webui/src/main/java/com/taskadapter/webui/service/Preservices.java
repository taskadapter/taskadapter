package com.taskadapter.webui.service;

import com.taskadapter.PluginManager;
import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.FSCredentialStore;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.webui.results.ExportResultStorage;

import java.io.File;

public class Preservices {
    public final File rootDir;
    public final EditorManager editorManager;
    public final PluginManager pluginManager = new PluginManager();
    public final SettingsManager settingsManager = new SettingsManager();
    public final LicenseManager licenseManager;

    public final String currentTaskAdapterVersion;
    public final TempFileManager tempFileManager;
    public final ConfigStorage configStorage;
    public final ExportResultStorage exportResultStorage;
    public final UIConfigStore uiConfigStore;
    public final BasicCredentialsManager credentialsManager;

    public Preservices(File rootDir, EditorManager editorManager) {
        this.rootDir = rootDir;
        this.editorManager = editorManager;

        this.currentTaskAdapterVersion = new CurrentVersionLoader().getCurrentVersion();
        this.tempFileManager = new TempFileManager(new File(rootDir, ".temporary-files"));

        licenseManager = new LicenseManager(rootDir);
        configStorage = new ConfigStorage(rootDir);
        exportResultStorage = new ExportResultStorage(rootDir, settingsManager.getMaxNumberOfResultsToKeep());

        CredentialsStore credentialsStore = new FSCredentialStore(rootDir);

        credentialsManager = new BasicCredentialsManager(credentialsStore, 50);

        uiConfigStore = new UIConfigStore(
                new UIConfigService(pluginManager, editorManager), configStorage, credentialsStore);
    }
}
