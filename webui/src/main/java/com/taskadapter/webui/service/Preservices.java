package com.taskadapter.webui.service;

import com.taskadapter.PluginManager;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.SettingsManager;

import java.io.File;

public class Preservices {
    public final File rootDir;
    public final EditorManager editorManager;
    public final PluginManager pluginManager = new PluginManager();
    public final SettingsManager settingsManager = new SettingsManager();
    public final LicenseManager licenseManager;

    public final String currentTaskAdapterVersion;
    public final TempFileManager tempFileManager;

    public Preservices(File rootDir, EditorManager editorManager) {
        this.rootDir = rootDir;
        this.editorManager = editorManager;

        this.currentTaskAdapterVersion = new CurrentVersionLoader().getCurrentVersion();
        this.tempFileManager = new TempFileManager(new File(rootDir,".temporary-files"));

        licenseManager = new LicenseManager(rootDir);
    }
}
