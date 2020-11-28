package com.taskadapter.vaadin14shim;

public class Button extends com.vaadin.ui.Button {

    public Button() {
        super();
    }

    public Button(String caption) {
        super(caption);
    }

    public Button(String caption, ClickListener listener) {
        super(caption, listener);
    }

    public void addClassName(String c) {
        addStyleName(c);
    }

    public void setDescription(String value) {
        super.setDescription(value);
    }
}
