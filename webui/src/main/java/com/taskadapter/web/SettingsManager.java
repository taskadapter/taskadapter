package com.taskadapter.web;

import java.util.prefs.Preferences;

public class SettingsManager {
    private static final String LICENSE_AGREEMENT_FLAG = "task_adapter_license_agreement_accepted";
    private static final boolean DEFAULT_LICENSE_AGREEMENT_ACCEPTED = false;

    private static final String FIELD_IS_LOCAL_MODE = "TALocal";
    private static final boolean DEFAULT_LOCAL = true;
    
    private static final String ALLOW_MANAGE_ALL_CONFIG = "admin_can_see_all_configs";

    private Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);

    public boolean isTAWorkingOnLocalMachine() {
        return prefs.getBoolean(FIELD_IS_LOCAL_MODE, DEFAULT_LOCAL);
    }

    public void setLocal(boolean local) {
        prefs.putBoolean(FIELD_IS_LOCAL_MODE, local);
    }

    public boolean isLicenseAgreementAccepted() {
        return prefs.getBoolean(LICENSE_AGREEMENT_FLAG, DEFAULT_LICENSE_AGREEMENT_ACCEPTED);
    }

    public void markLicenseAgreementAsAccepted() {
        prefs.putBoolean(LICENSE_AGREEMENT_FLAG, true);
    }
    
    public boolean adminCanManageAllConfigs() {
        return prefs.getBoolean(ALLOW_MANAGE_ALL_CONFIG, false);
    }
    
    public void setAdminCanManageAllConfigs(boolean canManage) {
        prefs.putBoolean(ALLOW_MANAGE_ALL_CONFIG, canManage);
    }
}
