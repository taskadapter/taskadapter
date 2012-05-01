package com.taskadapter;

public class LegacyConnectorsSupport {
    private static final String MSP_ID_LEGACY = "Microsoft Project (XML)";
    private static final String MSP_ID = "Microsoft Project";

    public static String getRealId(String pluginId) {
        String realId = pluginId;
        if (pluginId.equals(MSP_ID_LEGACY)) {
            realId = MSP_ID;
        }
        return realId;
    }
}
