package com.taskadapter.web.ui;

import com.vaadin.flow.component.html.Label;

public class HtmlLabel extends Label {
    public HtmlLabel(String htmlText) {
        setText(htmlText);
    }

    public void setText(String htmlText) {
        getElement().setProperty("innerHTML", htmlText);
    }
}
