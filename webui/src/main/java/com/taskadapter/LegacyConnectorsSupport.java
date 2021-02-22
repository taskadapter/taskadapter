package com.taskadapter;

import java.util.HashMap;
import java.util.Map;

public class LegacyConnectorsSupport {
    private static Map<String, String> oldToNewMap = new HashMap<>();

    static {
        oldToNewMap.put("Microsoft Project (XML)", "Microsoft Project");
        oldToNewMap.put("Redmine REST", "Redmine");

    }

    public static String getRealId(String pluginId) {
        String realId = pluginId;
        if (oldToNewMap.containsKey(pluginId)) {
            realId = oldToNewMap.get(pluginId);
        }
        return realId;
    }
}
