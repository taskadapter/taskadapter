package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;

import java.util.HashMap;
import java.util.Map;

public class MSPConfig extends ConnectorConfig {

    private static final Map<String, Integer> DEFAULT_PRIORITIES = new HashMap<>();
    static {
        DEFAULT_PRIORITIES.put("100", 100);
        DEFAULT_PRIORITIES.put("500", 500);
        DEFAULT_PRIORITIES.put("700", 700);
        DEFAULT_PRIORITIES.put("800", 800);
        DEFAULT_PRIORITIES.put("1000", 1000);
    }

    // TODO this is a UI element. move it to UI layer
    static final String DEFAULT_LABEL = "Microsoft Project";

    public MSPConfig() {
        super(DEFAULT_PRIORITIES);
        setLabel(DEFAULT_LABEL);
    }

    public MSPConfig(MSPConfig configToDeepClone) {
        super(configToDeepClone);
        setLabel(configToDeepClone.getLabel());
    }
}
