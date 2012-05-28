package com.taskadapter.web;

import java.util.prefs.Preferences;

/**
 * @author Alexey Skorokhodov
 */
public class SettingsManager {
    private static final boolean DEFAULT_LOCAL = true;
    private static final boolean DEFAULT_AGREEMENT_WAS_READ = false;

    private Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);

    public boolean isTAWorkingOnLocalMachine() {
        return prefs.getBoolean("TALocal", DEFAULT_LOCAL);
    }

    public void setLocal(boolean local) {
        prefs.putBoolean("TALocal", local);
    }

    public boolean isAgreementWasRead() {
        return prefs.getBoolean("AgreementWasRead", DEFAULT_AGREEMENT_WAS_READ);
    }

    public void setAgreementWasRead(boolean wasRead) {
        prefs.putBoolean("AgreementWasRead", wasRead);
    }
}
