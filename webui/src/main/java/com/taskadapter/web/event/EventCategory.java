package com.taskadapter.web.event;

public enum EventCategory {
    WebAppCategory("webapp"),
    SetupCategory("setup"),
    ConfigCategory("config"),
    ExportCategory("export"),
    UserCategory("user");

    private final String name;

    EventCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
