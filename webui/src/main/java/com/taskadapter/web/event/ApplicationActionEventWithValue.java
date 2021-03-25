package com.taskadapter.web.event;

public class ApplicationActionEventWithValue implements Event {
    private final EventCategory category;
    private final String action;
    private final String label;
    private final int value;

    public ApplicationActionEventWithValue(EventCategory category, String action, String label, int value) {
        this.category = category;
        this.action = action;
        this.label = label;
        this.value = value;
    }

    public EventCategory getCategory() {
        return category;
    }

    public String getAction() {
        return action;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}
