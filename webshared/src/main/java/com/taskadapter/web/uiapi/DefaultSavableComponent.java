package com.taskadapter.web.uiapi;

import com.vaadin.flow.component.Component;

public class DefaultSavableComponent implements SavableComponent {
    private final Component component;
    private final Runnable saveRunnable;

    public DefaultSavableComponent(Component component, Runnable saveRunnable) {
        this.component = component;
        this.saveRunnable = saveRunnable;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void save() {
        saveRunnable.run();
    }
}
