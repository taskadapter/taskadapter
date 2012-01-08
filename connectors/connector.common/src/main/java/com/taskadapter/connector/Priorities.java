package com.taskadapter.connector;

import java.util.Map;

public class Priorities {
    protected static final Integer DEFAULT_PRIORITY_VALUE = 500;

    /**
     * priority text -> priority number mappings.
     * <P>e.g. "low"->100
     */
    private Map<String, Integer> prioritiesMapping;

    public Priorities(Map<String, Integer> prioritiesMapping) {
        this.prioritiesMapping = prioritiesMapping;
    }

    /**
     * @return the nearest priority value for Tracker based on the MSP integer value (priority field).
     * Never NULL.
    e.g.
    Low: 100 : 0 - 100
    Trivial: 200 : 100-200
    Normal: 400 : 200-400
    High: 700 : 400-700
    Critical: 900 : 700-900
    Blocker: 1000 : 900-1000
     */
    public String getPriorityByMSP(Integer mspValue) {
        Integer minIntValue = 9999;
        String minStringValue = "";

        if (mspValue == null) {
            mspValue = DEFAULT_PRIORITY_VALUE;
        }

        for (Map.Entry<String, Integer> entry : prioritiesMapping.entrySet()) {
            if (entry.getValue() >= mspValue && entry.getValue() < minIntValue) {
                //check if mspValue in current interval and it's value smaller than minIntValue we should remember this key
                minIntValue = entry.getValue();
                minStringValue = entry.getKey();
            }
        }

        return minStringValue;
    }

    //get priority value for MSP based on the Tracker priority field
    public Integer getPriorityByText(String priorityText) {
        Integer priorityNumber = prioritiesMapping.get(priorityText);
        if (priorityNumber == null) {
            priorityNumber = DEFAULT_PRIORITY_VALUE;
        }
        return priorityNumber;
    }
}
