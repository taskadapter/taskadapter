package com.taskadapter.vaadin14shim;

public class PasswordField extends com.vaadin.ui.PasswordField {

    public void addClassName(String c) {
        addStyleName(c);
    }
}
