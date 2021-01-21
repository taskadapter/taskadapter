package com.taskadapter.web.uiapi;

import com.vaadin.flow.component.Component;

import java.util.concurrent.Callable;

public class DefaultSavableComponent implements SavableComponent {
    private final Component component;
    private final Callable<Boolean> saveRunnable;

    public DefaultSavableComponent(Component component, Callable<Boolean> saveRunnable) {
        this.component = component;
        this.saveRunnable = saveRunnable;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public boolean save() {
        try {
            return saveRunnable.call();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
