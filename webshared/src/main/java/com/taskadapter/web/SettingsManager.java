package com.taskadapter.web;

import java.util.prefs.Preferences;

public class SettingsManager {
    private static final boolean DEFAULT_LOCAL = true;
    private static final boolean DEFAULT_LICENSE_AGREEMENT_ACCEPTED = false;
    public static final String LICENSE_AGREEMENT_FLAG = "AgreementWasRead";

    private Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);

    public boolean isTAWorkingOnLocalMachine() {
        return prefs.getBoolean("TALocal", DEFAULT_LOCAL);
    }

    public void setLocal(boolean local) {
        prefs.putBoolean("TALocal", local);
    }

    public boolean isLicenseAgreementAccepted() {
        return prefs.getBoolean(LICENSE_AGREEMENT_FLAG, DEFAULT_LICENSE_AGREEMENT_ACCEPTED);
    }

    public void markLicenseAgreementAsAccepted() {
        prefs.putBoolean(LICENSE_AGREEMENT_FLAG, true);
    }
}
