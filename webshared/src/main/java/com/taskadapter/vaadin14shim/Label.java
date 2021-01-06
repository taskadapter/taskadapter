package com.taskadapter.vaadin14shim;

import com.vaadin.shared.ui.label.ContentMode;

public class Label extends com.vaadin.ui.Label {

    public Label() {
        super();
    }

    public Label(String str) {
        super(str);
    }

    public Label(String str, ContentMode mode) {
        super(str, mode);
    }

    public void addClassName(String c) {
        addStyleName(c);
    }

    public void setText(String value) {
        setValue(value);
    }
}
