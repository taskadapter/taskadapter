package com.taskadapter.vaadin14shim;

import com.vaadin.ui.Component;

public class VerticalLayout extends com.vaadin.ui.VerticalLayout {

    public VerticalLayout(Component... children) {
        super(children);
    }

    public void add(Component c) {
        addComponent(c);
    }

    public void setClassName(String c) {
        setStyleName(c);
    }

    public void removeAll() {
        removeAllComponents();
    }
}

