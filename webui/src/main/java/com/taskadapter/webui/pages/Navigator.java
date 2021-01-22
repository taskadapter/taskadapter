package com.taskadapter.webui.pages;

import com.vaadin.flow.component.UI;

public class Navigator {

    public static final String NEW_SETUP = "new-setup";
    public static final String NEW_CONFIG = "new-config";
    public static final String SETUPS_LIST = "setups-list";
    public static final String CONFIGS_LIST = "configs-list";
    public static final String SCHEDULES_LIST = "schedules-list";
    public static final String CONFIGURE_SYSTEM = "configure-system";
    public static final String SUPPORT = "support";
    public static final String PROFILE = "user-profile";
    public static final String LOGIN = "login";
    public static final String HOME = "";
    public static final String RESULTS_LIST = "results-list";
    public static final String RESULT = "result";

    public static void newConfig() {
        UI.getCurrent().navigate(NEW_CONFIG);
    }

    public static void newSetup() {
        UI.getCurrent().navigate(NEW_SETUP);
    }

    public static void setupsList() {
        UI.getCurrent().navigate(SETUPS_LIST);
    }

    public static void configsList() {
        UI.getCurrent().navigate(CONFIGS_LIST);
    }

    public static void schedulesList() {
        UI.getCurrent().navigate(SCHEDULES_LIST);
    }

    public static void configureSystem() {
        UI.getCurrent().navigate(CONFIGURE_SYSTEM);
    }

    public static void support() {
        UI.getCurrent().navigate(SUPPORT);
    }

    public static void profile() {
        UI.getCurrent().navigate(PROFILE);
    }

    public static void login() {
        UI.getCurrent().navigate(LOGIN);
    }

    public static void home() {
        UI.getCurrent().navigate(HOME);
    }

    public static void result(String resultId) {
        UI.getCurrent().navigate(RESULT + "/" + resultId);
    }

    public static void resultsList() {
        UI.getCurrent().navigate(RESULTS_LIST);
    }
}
