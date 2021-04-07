package com.taskadapter.web;

import com.taskadapter.web.event.EventBusImpl;
import com.taskadapter.web.event.SchedulerStatusChanged;

import java.util.prefs.Preferences;

public class SettingsManager {
    private final static String LICENSE_AGREEMENT_FLAG = "task_adapter_license_agreement_accepted";
    private final static boolean DEFAULT_LICENSE_AGREEMENT_ACCEPTED = false;

    private final static String ERROR_REPORTING_ENABLED = "task_adapter.error_reporting_enabled";
    private final static boolean ERROR_REPORTING_DEFAULT_STATE = true;

    private final static String FIELD_IS_LOCAL_MODE = "TALocal";
    private final static boolean DEFAULT_LOCAL = true;

    private final static String ALLOW_MANAGE_ALL_CONFIG = "admin_can_see_all_configs";
    private final static String SCHEDULER_ENABLED = "scheduler_enabled";
    private final static String MAX_NUMBER_RESULTS_TO_KEEP = "max_number_of_results_to_keep";
    private final static int DEFAULT_MAX_NUMBER_OF_RESULTS = 100000;

    private final static Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);

    public static boolean isTAWorkingOnLocalMachine() {
        return prefs.getBoolean(FIELD_IS_LOCAL_MODE, DEFAULT_LOCAL);
    }

    public static void setLocal(boolean local) {
        prefs.putBoolean(FIELD_IS_LOCAL_MODE, local);
    }

    public static boolean isLicenseAgreementAccepted() {
        return prefs.getBoolean(LICENSE_AGREEMENT_FLAG, DEFAULT_LICENSE_AGREEMENT_ACCEPTED);
    }

    public static boolean isErrorReportingEnabled() {
        return prefs.getBoolean(ERROR_REPORTING_ENABLED, ERROR_REPORTING_DEFAULT_STATE);
    }

    public static void markLicenseAgreementAsAccepted() {
        prefs.putBoolean(LICENSE_AGREEMENT_FLAG, true);
    }

    public static void setErrorReporting(boolean enabled) {
        prefs.putBoolean(ERROR_REPORTING_ENABLED, enabled);
    }

    public static int getMaxNumberOfResultsToKeep() {
        return prefs.getInt(MAX_NUMBER_RESULTS_TO_KEEP, DEFAULT_MAX_NUMBER_OF_RESULTS);
    }

    public static void setMaxNumberOfResultsToKeep(int value) {
        prefs.putInt(MAX_NUMBER_RESULTS_TO_KEEP, value);
    }

    public static boolean adminCanManageAllConfigs() {
        return prefs.getBoolean(ALLOW_MANAGE_ALL_CONFIG, false);
    }

    public static void setAdminCanManageAllConfigs(boolean canManage) {
        prefs.putBoolean(ALLOW_MANAGE_ALL_CONFIG, canManage);
    }

    public static boolean schedulerEnabled() {
        return prefs.getBoolean(SCHEDULER_ENABLED, false);
    }

    public static void setSchedulerEnabled(boolean flag) {
        prefs.putBoolean(SCHEDULER_ENABLED, flag);
        EventBusImpl.post(new SchedulerStatusChanged(flag));
    }
}