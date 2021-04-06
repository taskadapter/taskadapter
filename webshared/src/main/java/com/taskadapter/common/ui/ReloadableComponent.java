package com.taskadapter.common.ui;

import com.vaadin.flow.component.Component;

public interface ReloadableComponent {
    void reload();

    Component getComponent();
}
