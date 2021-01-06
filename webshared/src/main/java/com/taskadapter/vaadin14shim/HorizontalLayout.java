package com.taskadapter.vaadin14shim;

import com.vaadin.ui.Component;

public class HorizontalLayout extends com.vaadin.ui.HorizontalLayout {

    public HorizontalLayout(Component... children) {
        super(children);
    }

    public void add(Component c) {
        addComponent(c);
    }

    public void setClassName(String c) {
        setStyleName(c);
    }

    public void addClassName(String c) {
        addStyleName(c);
    }

    public void removeAll() {
        removeAllComponents();
    }
}
