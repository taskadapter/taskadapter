package com.taskadapter.webui.service;

import java.io.File;

import com.taskadapter.PluginManager;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UIConfigStore;

/** Pre-services services. */
public class Preservices {
    public final File rootDir;
    public final EditorManager editorManager;
    public final PluginManager pluginManager = new PluginManager();
    public final SettingsManager settingsManager = new SettingsManager();
    public final LicenseManager licenseManager;
    public final UIConfigStore uiConfigStore;

    public final String currentTaskAdapterVersion;

    public Preservices(File rootDir, EditorManager editorManager,
            CredentialsManager credentialManager) {
        this.rootDir = rootDir;
        this.editorManager = editorManager;
        final ConfigStorage configStorage = new ConfigStorage(rootDir);

        this.uiConfigStore = new UIConfigStore(new UIConfigService(
                pluginManager, editorManager), configStorage);

        this.currentTaskAdapterVersion = new CurrentVersionLoader()
                .getCurrentVersion();

        licenseManager = new LicenseManager(rootDir);

    }

}
