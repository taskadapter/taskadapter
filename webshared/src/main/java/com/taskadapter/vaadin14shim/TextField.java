package com.taskadapter.vaadin14shim;

public class TextField extends com.vaadin.ui.TextField {

    public void addClassName(String c) {
        addStyleName(c);
    }
}
