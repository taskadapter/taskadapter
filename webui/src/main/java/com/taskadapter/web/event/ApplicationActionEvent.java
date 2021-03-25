package com.taskadapter.web.event;

public class ApplicationActionEvent implements Event {
    private final EventCategory category;
    private final String action;
    private final String label;

    public ApplicationActionEvent(EventCategory category, String action, String label) {
        this.category = category;
        this.action = action;
        this.label = label;
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
}
