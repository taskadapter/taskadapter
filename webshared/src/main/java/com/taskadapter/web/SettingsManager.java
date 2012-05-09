package com.taskadapter.web;

import java.util.prefs.Preferences;

/**
 * @author Alexey Skorokhodov
 */
public class SettingsManager {
    private static final boolean DEFAULT_LOCAL = true;

    private Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);

    /**
     * Is TA working on the local machine?
     */
    public boolean isLocal() {
        return prefs.getBoolean("TALocal", DEFAULT_LOCAL);
    }

    public void setLocal(boolean local) {
        prefs.putBoolean("TALocal", local);
    }
}
