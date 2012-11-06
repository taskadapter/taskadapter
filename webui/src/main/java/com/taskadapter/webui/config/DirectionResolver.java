package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.web.uiapi.UISyncConfig;

public final class DirectionResolver {
    // TODO give a better name
    public static UISyncConfig getDirectionalConfig(UISyncConfig config, MappingSide exportDirection) {
        switch (exportDirection) {
            case RIGHT:
                return config;
            case LEFT:
                return config.reverse();
            default:
                throw new IllegalArgumentException("Unsupported mapping direction " + exportDirection);
        }
    }
}
