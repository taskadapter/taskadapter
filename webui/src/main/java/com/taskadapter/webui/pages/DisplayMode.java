package com.taskadapter.webui.pages;

import com.taskadapter.web.uiapi.UISyncConfig;

/**
 * Config display mode.
 */
public enum DisplayMode {
    /**
     * This page is displaying only owned configs.
     */
    OWNED_CONFIGS {
        String nameOf(UISyncConfig config) {
            return config.getLabel();
        }
    },

    /**
     * This page is displaying all configs.
     */
    ALL_CONFIGS {
        String nameOf(UISyncConfig config) {
            return config.getOwnerName() + " : " + config.getLabel();
        }
    };

    /**
     * Provides a name of the config.
     */
    abstract String nameOf(UISyncConfig config);
}
