package com.taskadapter.web.uiapi;

import com.vaadin.flow.component.Component;

public interface SavableComponent {
    Component getComponent();

    void save();
}
